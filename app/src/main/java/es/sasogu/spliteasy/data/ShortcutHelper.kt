package es.sasogu.spliteasy.data

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import es.sasogu.spliteasy.MainActivity
import es.sasogu.spliteasy.R
import es.sasogu.spliteasy.domain.AppPair

/**
 * Accesos directos anclados al lanzador mediante la API oficial de shortcuts.
 *
 * El acceso directo abre MainActivity con la pareja indicada. Si el dispositivo
 * no permite la apertura automática, la actividad muestra directamente la guía.
 */
object ShortcutHelper {
    const val ACTION_OPEN_PAIR = "es.sasogu.spliteasy.action.OPEN_PAIR"
    const val EXTRA_PAIR_ID = "es.sasogu.spliteasy.extra.PAIR_ID"

    sealed interface PinResult {
        data object Requested : PinResult
        data object Unsupported : PinResult
        data object Failed : PinResult
    }

    fun createPairIntent(context: Context, pairId: String): Intent =
        Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_PAIR
            putExtra(EXTRA_PAIR_ID, pairId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

    fun pin(context: Context, pair: AppPair): PinResult {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            return PinResult.Unsupported
        }

        val label = pair.displayName.ifBlank { context.getString(R.string.app_name) }
        val shortcut = ShortcutInfoCompat.Builder(context, shortcutId(pair.id))
            .setShortLabel(label.take(40))
            .setLongLabel(label)
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_split))
            .setIntent(createPairIntent(context, pair.id))
            .build()

        return if (ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)) {
            PinResult.Requested
        } else {
            PinResult.Failed
        }
    }

    private fun shortcutId(pairId: String): String = "pair-$pairId"
}
