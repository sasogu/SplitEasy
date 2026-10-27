package es.sasogu.spliteasy.domain

/**
 * Nivel de multiventana que SplitEasy puede justificar con datos públicos.
 *
 * No se deduce de la versión de Android: la pantalla dividida depende del
 * fabricante, del modelo y de la configuración del dispositivo.
 */
enum class CapabilityLevel {
    /** Se ha observado o confirmado pantalla dividida real en este dispositivo. */
    SPLIT_CONFIRMED,

    /** El dispositivo declara ventanas flotantes y podemos intentar colocarlas. */
    WINDOWED,

    /** Solo podemos guiar al usuario con el flujo del sistema. */
    ASSISTED,

    /** No hay indicios de multiventana (por ejemplo, dispositivo de poca RAM). */
    UNCONFIRMED,
}

/** Datos del dispositivo obtenidos únicamente con APIs públicas. */
data class DeviceFacts(
    val sdkInt: Int,
    val manufacturer: String,
    val brand: String,
    val model: String,
    val freeformWindowManagement: Boolean,
    val lowRamDevice: Boolean,
    val smallestScreenWidthDp: Int,
    val navigationBottomInsetDp: Int = 0,
    val navigationLeftInsetDp: Int = 0,
    val navigationRightInsetDp: Int = 0,
) {
    val isTablet: Boolean get() = smallestScreenWidthDp >= 600
}

/** Lo que SplitEasy ha aprendido en este dispositivo concreto. */
data class CapabilityEvidence(
    val splitConfirmed: Boolean = false,
    val freeformConfirmed: Boolean = false,
    val freeformFailures: Int = 0,
    val experimentalAdjacentEnabled: Boolean = false,
) {
    val automaticAttemptsAllowed: Boolean
        get() = freeformFailures < MAX_FREEFORM_FAILURES

    companion object {
        /**
         * Tras dos intentos fallidos dejamos de ofrecer la apertura automática
         * como acción principal. El usuario siempre puede reintentarla.
         */
        const val MAX_FREEFORM_FAILURES = 2
    }
}

data class CapabilityReport(
    val level: CapabilityLevel,
    val canAttemptFreeform: Boolean,
    val freeformFailures: Int,
    val experimentalAdjacentEnabled: Boolean,
) {
    val freeformExhausted: Boolean
        get() = freeformFailures >= CapabilityEvidence.MAX_FREEFORM_FAILURES
}

/**
 * Decide qué se le puede prometer al usuario.
 *
 * Reglas:
 * - La pantalla dividida solo se muestra como compatible si hay evidencia real
 *   (nuestro activity ha entrado en multiventana o el usuario lo ha confirmado).
 * - Las ventanas flotantes requieren `FEATURE_FREEFORM_WINDOW_MANAGEMENT`.
 * - En un dispositivo de poca RAM, sin evidencia, no se afirma compatibilidad.
 */
object CapabilityEvaluator {
    fun evaluate(
        facts: DeviceFacts,
        evidence: CapabilityEvidence,
    ): CapabilityReport {
        val freeformReady = facts.freeformWindowManagement &&
            !facts.lowRamDevice &&
            evidence.automaticAttemptsAllowed

        val level = when {
            evidence.splitConfirmed -> CapabilityLevel.SPLIT_CONFIRMED
            freeformReady -> CapabilityLevel.WINDOWED
            facts.lowRamDevice && !evidence.freeformConfirmed -> CapabilityLevel.UNCONFIRMED
            else -> CapabilityLevel.ASSISTED
        }

        return CapabilityReport(
            level = level,
            canAttemptFreeform = freeformReady,
            freeformFailures = evidence.freeformFailures,
            experimentalAdjacentEnabled = evidence.experimentalAdjacentEnabled,
        )
    }
}
