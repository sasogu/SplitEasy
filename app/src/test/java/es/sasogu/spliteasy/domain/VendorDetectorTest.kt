package es.sasogu.spliteasy.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class VendorDetectorTest {
    @Test
    fun `detects samsung`() {
        assertEquals(GuideVendor.SAMSUNG, VendorDetector.detect("samsung", "samsung"))
    }

    @Test
    fun `detects xiaomi and its brands`() {
        assertEquals(GuideVendor.XIAOMI, VendorDetector.detect("Xiaomi", "Xiaomi"))
        assertEquals(GuideVendor.XIAOMI, VendorDetector.detect("Xiaomi", "Redmi"))
        assertEquals(GuideVendor.XIAOMI, VendorDetector.detect("Xiaomi", "POCO"))
    }

    @Test
    fun `detects motorola`() {
        assertEquals(GuideVendor.MOTOROLA, VendorDetector.detect("motorola", "moto"))
    }

    @Test
    fun `detects pixel`() {
        assertEquals(GuideVendor.PIXEL, VendorDetector.detect("Google", "google"))
        assertEquals(GuideVendor.PIXEL, VendorDetector.detect("unknown", "Pixel"))
    }

    @Test
    fun `unknown manufacturer falls back to generic`() {
        assertEquals(GuideVendor.GENERIC, VendorDetector.detect("Fairphone", "FP"))
        assertEquals(GuideVendor.GENERIC, VendorDetector.detect(null, null))
        assertEquals(GuideVendor.GENERIC, VendorDetector.detect("", ""))
    }
}
