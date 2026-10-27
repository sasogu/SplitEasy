package es.sasogu.spliteasy.domain

import kotlin.math.max

/** Rectángulo en píxeles, independiente de `android.graphics.Rect` para poder probarlo en JVM. */
data class WindowRect(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
) {
    val width: Int get() = right - left
    val height: Int get() = bottom - top
    val isValid: Boolean get() = width > 0 && height > 0

    fun insetBy(pixels: Int): WindowRect {
        val p = max(0, pixels)
        return WindowRect(left + p, top + p, right - p, bottom - p)
    }
}

/**
 * Calcula dos mitades para lanzar aplicaciones en ventanas flotantes.
 *
 * `FEATURE_FREEFORM_WINDOW_MANAGEMENT` indica que el sistema admite ventanas
 * con límites propios. La API pública `ActivityOptions.setLaunchBounds` usa
 * esas coordenadas; el dispositivo puede ignorarlas o recolocar las ventanas,
 * por lo que el resultado se confirma con el usuario.
 */
object SplitBoundsCalculator {
    fun halves(
        display: WindowRect,
        marginPx: Int,
        gapPx: Int,
        sideBySide: Boolean,
    ): Pair<WindowRect, WindowRect>? {
        if (!display.isValid) return null
        val usable = display.insetBy(marginPx)
        if (!usable.isValid) return null
        val gap = max(0, gapPx)

        if (sideBySide) {
            if (usable.width <= gap) return null
            val middle = usable.left + (usable.width - gap) / 2
            val first = WindowRect(usable.left, usable.top, middle, usable.bottom)
            val second = WindowRect(middle + gap, usable.top, usable.right, usable.bottom)
            return if (first.isValid && second.isValid) first to second else null
        }

        if (usable.height <= gap) return null
        val middle = usable.top + (usable.height - gap) / 2
        val first = WindowRect(usable.left, usable.top, usable.right, middle)
        val second = WindowRect(usable.left, middle + gap, usable.right, usable.bottom)
        return if (first.isValid && second.isValid) first to second else null
    }
}
