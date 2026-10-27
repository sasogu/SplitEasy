package es.sasogu.spliteasy.data

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import es.sasogu.spliteasy.domain.AppEntry
import es.sasogu.spliteasy.domain.SplitBoundsCalculator
import es.sasogu.spliteasy.domain.WindowRect
import kotlin.math.roundToInt

/**
 * Lanzamientos reales permitidos por APIs públicas.
 *
 * Estrategias:
 * - [launchSequential]: `startActivities` abre las dos aplicaciones de una vez
 *   para que queden en Recientes. Es la preparación fiable para la guía manual.
 * - [launchFreeform]: usa `ActivityOptions.setLaunchBounds` en dispositivos que
 *   declaran `FEATURE_FREEFORM_WINDOW_MANAGEMENT`. No usa APIs ocultas.
 * - [launchAdjacent]: opción heredada `FLAG_ACTIVITY_LAUNCH_ADJACENT`, solo para
 *   la prueba experimental. En Android 12+ normalmente no crea la pareja.
 *
 * No se usa `setLaunchWindowingMode`: es @hide/@TestApi y requiere privilegios
 * del sistema.
 */
class SplitLaunchController(private val context: Context) {
    private val packageManager = context.packageManager
    private val mainHandler = Handler(Looper.getMainLooper())

    fun launch(app: AppEntry): Boolean {
        val intent = launchIntentFor(app) ?: return false
        return runCatching { context.startActivity(intent) }.isSuccess
    }

    fun launchSequential(first: AppEntry, second: AppEntry): Boolean {
        val intents = listOfNotNull(launchIntentFor(first), launchIntentFor(second))
        if (intents.isEmpty()) return false
        return runCatching {
            context.startActivities(intents.toTypedArray(), null)
        }.isSuccess
    }

    fun launchFreeform(first: AppEntry, second: AppEntry): Boolean {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_FREEFORM_WINDOW_MANAGEMENT)) {
            return false
        }

        val bounds = freeformHalves() ?: return false
        val firstIntent = launchIntentFor(first) ?: return false
        val secondIntent = launchIntentFor(second) ?: return false

        val firstStarted = runCatching {
            context.startActivity(firstIntent, launchBoundsOptions(bounds.first))
        }.isSuccess
        if (!firstStarted) return false

        // Mantener SplitEasy parcialmente visible detrás de la ventana flotante
        // ayuda a que Android permita el segundo lanzamiento en segundo plano.
        mainHandler.postDelayed({
            runCatching {
                context.startActivity(secondIntent, launchBoundsOptions(bounds.second))
            }
        }, FREEFORM_SECOND_LAUNCH_DELAY_MS)
        return true
    }

    fun launchAdjacent(first: AppEntry, second: AppEntry): Boolean {
        val firstIntent = launchIntentFor(first) ?: return false
        val secondIntent = launchIntentFor(second)?.apply {
            addFlags(
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
                    Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT,
            )
        } ?: return false

        val firstStarted = runCatching { context.startActivity(firstIntent) }.isSuccess
        if (!firstStarted) return false

        mainHandler.postDelayed({
            runCatching { context.startActivity(secondIntent) }
        }, ADJACENT_SECOND_LAUNCH_DELAY_MS)
        return true
    }

    private fun launchIntentFor(app: AppEntry): Intent? {
        val explicit = app.activityName?.let { activityName ->
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = android.content.ComponentName(app.packageName, activityName)
            }
        }
        val intent = explicit ?: packageManager.getLaunchIntentForPackage(app.packageName)
        return intent?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    }

    private fun launchBoundsOptions(bounds: WindowRect): android.os.Bundle =
        ActivityOptions.makeBasic()
            .setLaunchBounds(bounds.toRect())
            .toBundle()

    private fun freeformHalves(): Pair<WindowRect, WindowRect>? {
        val display = displayBounds()
        val density = context.resources.displayMetrics.density
        val marginPx = (SIDE_MARGIN_DP * density).roundToInt()
        val gapPx = (CENTER_GAP_DP * density).roundToInt()
        return SplitBoundsCalculator.halves(
            display = display,
            marginPx = marginPx,
            gapPx = gapPx,
            sideBySide = display.width >= display.height,
        )
    }

    private fun displayBounds(): WindowRect {
        val windowManager = context.getSystemService(WindowManager::class.java)
        val bounds = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                windowManager.currentWindowMetrics.bounds
            } else {
                @Suppress("DEPRECATION")
                val display = windowManager.defaultDisplay
                val metrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                display.getRealMetrics(metrics)
                Rect(0, 0, metrics.widthPixels, metrics.heightPixels)
            }
        }.getOrElse {
            val metrics = context.resources.displayMetrics
            Rect(0, 0, metrics.widthPixels, metrics.heightPixels)
        }

        return WindowRect(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    private fun WindowRect.toRect(): Rect = Rect(left, top, right, bottom)

    private companion object {
        const val FREEFORM_SECOND_LAUNCH_DELAY_MS = 650L
        const val ADJACENT_SECOND_LAUNCH_DELAY_MS = 450L
        const val SIDE_MARGIN_DP = 10f
        const val CENTER_GAP_DP = 8f
    }
}
