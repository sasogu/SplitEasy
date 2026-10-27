package es.sasogu.spliteasy.domain

import java.util.Locale

enum class GuideVendor {
    SAMSUNG,
    XIAOMI,
    MOTOROLA,
    PIXEL,
    GENERIC,
}

/**
 * Detecta el fabricante con datos públicos (`Build.MANUFACTURER` y
 * `Build.BRAND`). No se usa para afirmar compatibilidad, solo para elegir el
 * texto orientativo de la guía. Si hay duda se devuelve [GuideVendor.GENERIC].
 */
object VendorDetector {
    fun detect(manufacturer: String?, brand: String?): GuideVendor {
        val value = listOfNotNull(manufacturer, brand)
            .joinToString(" ")
            .lowercase(Locale.ROOT)

        return when {
            "samsung" in value -> GuideVendor.SAMSUNG
            "xiaomi" in value || "redmi" in value || "poco" in value -> GuideVendor.XIAOMI
            "motorola" in value -> GuideVendor.MOTOROLA
            "google" in value || "pixel" in value -> GuideVendor.PIXEL
            else -> GuideVendor.GENERIC
        }
    }
}
