package es.sasogu.spliteasy.domain

enum class NavigationMode {
    GESTURES,
    THREE_BUTTON,
    UNKNOWN,
}

/**
 * Heurística basada en el tamaño de los insets de la barra de navegación.
 *
 * Android no expone una API pública que distinga gestos de tres botones.
 * Los valores típicos son 24-32 dp con gestos y 48 dp con tres botones, pero
 * cada fabricante puede variar. Por eso el resultado es solo una sugerencia y
 * la interfaz permite corregirlo.
 */
object NavigationModeEstimator {
    private const val THREE_BUTTON_MIN_DP = 40
    private const val GESTURE_MIN_DP = 16

    fun estimate(
        bottomInsetDp: Int,
        leftInsetDp: Int,
        rightInsetDp: Int,
    ): NavigationMode {
        val sideInsetDp = maxOf(leftInsetDp, rightInsetDp)
        return when {
            bottomInsetDp >= THREE_BUTTON_MIN_DP || sideInsetDp >= THREE_BUTTON_MIN_DP ->
                NavigationMode.THREE_BUTTON
            bottomInsetDp in GESTURE_MIN_DP until THREE_BUTTON_MIN_DP ->
                NavigationMode.GESTURES
            else -> NavigationMode.UNKNOWN
        }
    }
}
