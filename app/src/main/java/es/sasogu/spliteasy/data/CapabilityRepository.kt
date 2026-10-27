package es.sasogu.spliteasy.data

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import es.sasogu.spliteasy.domain.CapabilityEvidence
import es.sasogu.spliteasy.domain.DeviceFacts
import es.sasogu.spliteasy.domain.NavigationMode
import kotlin.math.min

/**
 * Hechos del dispositivo y evidencia aprendida entre sesiones.
 *
 * La evidencia nunca se deduce de `Build.VERSION.SDK_INT`: la pantalla dividida
 * es una capacidad del producto que puede estar deshabilitada por el fabricante.
 */
class CapabilityRepository(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun deviceFacts(): DeviceFacts {
        val packageManager = appContext.packageManager
        val activityManager = appContext.getSystemService(ActivityManager::class.java)
        return DeviceFacts(
            sdkInt = Build.VERSION.SDK_INT,
            manufacturer = Build.MANUFACTURER.orEmpty(),
            brand = Build.BRAND.orEmpty(),
            model = Build.MODEL.orEmpty(),
            freeformWindowManagement = packageManager.hasSystemFeature(
                PackageManager.FEATURE_FREEFORM_WINDOW_MANAGEMENT,
            ),
            lowRamDevice = activityManager?.isLowRamDevice == true,
            smallestScreenWidthDp = appContext.resources.configuration.smallestScreenWidthDp,
        )
    }

    fun evidence(): CapabilityEvidence = CapabilityEvidence(
        splitConfirmed = preferences.getBoolean(KEY_SPLIT_CONFIRMED, false),
        freeformConfirmed = preferences.getBoolean(KEY_FREEFORM_CONFIRMED, false),
        freeformFailures = preferences.getInt(KEY_FREEFORM_FAILURES, 0),
        experimentalAdjacentEnabled = preferences.getBoolean(KEY_EXPERIMENTAL, false),
    )

    fun markSplitConfirmed() {
        preferences.edit().putBoolean(KEY_SPLIT_CONFIRMED, true).apply()
    }

    fun recordFreeformSuccess() {
        preferences.edit()
            .putBoolean(KEY_FREEFORM_CONFIRMED, true)
            .putInt(KEY_FREEFORM_FAILURES, 0)
            .apply()
    }

    fun recordFreeformFailure() {
        val failures = min(
            9,
            preferences.getInt(KEY_FREEFORM_FAILURES, 0) + 1,
        )
        preferences.edit().putInt(KEY_FREEFORM_FAILURES, failures).apply()
    }

    fun setExperimentalAdjacentEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_EXPERIMENTAL, enabled).apply()
    }

    fun navigationOverride(): NavigationMode? =
        preferences.getString(KEY_NAVIGATION_OVERRIDE, null)?.let { name ->
            runCatching { NavigationMode.valueOf(name) }.getOrNull()
        }

    fun setNavigationOverride(mode: NavigationMode?) {
        preferences.edit().apply {
            if (mode == null) remove(KEY_NAVIGATION_OVERRIDE)
            else putString(KEY_NAVIGATION_OVERRIDE, mode.name)
        }.apply()
    }

    /** Olvida la evidencia aprendida, pero conserva las parejas y las opciones. */
    fun resetEvidence() {
        preferences.edit()
            .remove(KEY_SPLIT_CONFIRMED)
            .remove(KEY_FREEFORM_CONFIRMED)
            .remove(KEY_FREEFORM_FAILURES)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "spliteasy_capability"
        const val KEY_SPLIT_CONFIRMED = "split_confirmed"
        const val KEY_FREEFORM_CONFIRMED = "freeform_confirmed"
        const val KEY_FREEFORM_FAILURES = "freeform_failures"
        const val KEY_EXPERIMENTAL = "experimental_adjacent"
        const val KEY_NAVIGATION_OVERRIDE = "navigation_override"
    }
}
