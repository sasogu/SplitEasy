package es.sasogu.spliteasy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CapabilityEvaluatorTest {
    private fun facts(
        freeform: Boolean = false,
        lowRam: Boolean = false,
        freeformDev: Boolean = false,
        forceResizable: Boolean = false,
    ) = DeviceFacts(
        sdkInt = 35,
        manufacturer = "Test",
        brand = "test",
        model = "Test",
        freeformWindowManagement = freeform,
        lowRamDevice = lowRam,
        smallestScreenWidthDp = 400,
        freeformDeveloperOption = freeformDev,
        forceResizableActivities = forceResizable,
    )

    @Test
    fun `split evidence wins over everything else`() {
        val report = CapabilityEvaluator.evaluate(
            facts(freeform = false, lowRam = true),
            CapabilityEvidence(splitConfirmed = true),
        )

        assertEquals(CapabilityLevel.SPLIT_CONFIRMED, report.level)
    }

    @Test
    fun `freeform device can attempt windows`() {
        val report = CapabilityEvaluator.evaluate(
            facts(freeform = true),
            CapabilityEvidence(),
        )

        assertEquals(CapabilityLevel.WINDOWED, report.level)
        assertTrue(report.canAttemptFreeform)
    }

    @Test
    fun `normal phone is assisted`() {
        val report = CapabilityEvaluator.evaluate(
            facts(),
            CapabilityEvidence(),
        )

        assertEquals(CapabilityLevel.ASSISTED, report.level)
        assertFalse(report.canAttemptFreeform)
    }

    @Test
    fun `low ram without evidence is unconfirmed`() {
        val report = CapabilityEvaluator.evaluate(
            facts(lowRam = true),
            CapabilityEvidence(),
        )

        assertEquals(CapabilityLevel.UNCONFIRMED, report.level)
    }

    @Test
    fun `two freeform failures disable automatic attempts`() {
        val report = CapabilityEvaluator.evaluate(
            facts(freeform = true),
            CapabilityEvidence(freeformFailures = CapabilityEvidence.MAX_FREEFORM_FAILURES),
        )

        assertEquals(CapabilityLevel.ASSISTED, report.level)
        assertFalse(report.canAttemptFreeform)
        assertTrue(report.freeformExhausted)
    }

    @Test
    fun `developer freeform option enables windowed mode`() {
        val report = CapabilityEvaluator.evaluate(
            facts(freeformDev = true, forceResizable = true),
            CapabilityEvidence(),
        )

        assertEquals(CapabilityLevel.WINDOWED, report.level)
        assertTrue(report.canAttemptFreeform)
        assertTrue(report.freeformViaDeveloperOption)
    }

    @Test
    fun `developer freeform option without force resizable is not enough`() {
        val report = CapabilityEvaluator.evaluate(
            facts(freeformDev = true, forceResizable = false),
            CapabilityEvidence(),
        )

        assertEquals(CapabilityLevel.ASSISTED, report.level)
        assertFalse(report.canAttemptFreeform)
    }

    @Test
    fun `system freeform is not marked as developer option`() {
        val report = CapabilityEvaluator.evaluate(
            facts(freeform = true),
            CapabilityEvidence(),
        )

        assertFalse(report.freeformViaDeveloperOption)
    }

    @Test
    fun `developer freeform overrides low ram heuristic`() {
        val report = CapabilityEvaluator.evaluate(
            facts(lowRam = true, freeformDev = true, forceResizable = true),
            CapabilityEvidence(),
        )

        assertEquals(CapabilityLevel.WINDOWED, report.level)
        assertTrue(report.canAttemptFreeform)
    }

    @Test
    fun `android version alone never confirms split screen`() {
        val modernFacts = facts().copy(sdkInt = 36)
        val report = CapabilityEvaluator.evaluate(modernFacts, CapabilityEvidence())

        assertEquals(CapabilityLevel.ASSISTED, report.level)
    }
}
