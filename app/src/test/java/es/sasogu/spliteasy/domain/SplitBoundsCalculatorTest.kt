package es.sasogu.spliteasy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SplitBoundsCalculatorTest {
    @Test
    fun `landscape splits left and right with margin and gap`() {
        val halves = SplitBoundsCalculator.halves(
            display = WindowRect(0, 0, 2400, 1080),
            marginPx = 10,
            gapPx = 8,
            sideBySide = true,
        )

        assertNotNull(halves)
        val (left, right) = halves!!
        assertEquals(10, left.left)
        assertEquals(10, left.top)
        assertEquals(1196, left.right)
        assertEquals(1070, left.bottom)
        assertEquals(1204, right.left)
        assertEquals(2390, right.right)
        assertEquals(8, right.left - left.right)
    }

    @Test
    fun `portrait splits top and bottom`() {
        val halves = SplitBoundsCalculator.halves(
            display = WindowRect(0, 0, 1080, 2400),
            marginPx = 10,
            gapPx = 8,
            sideBySide = false,
        )

        assertNotNull(halves)
        val (top, bottom) = halves!!
        assertEquals(top.bottom + 8, bottom.top)
        assertEquals(10, top.left)
        assertEquals(1070, top.right)
    }

    @Test
    fun `odd width shares the extra pixel`() {
        val halves = SplitBoundsCalculator.halves(
            display = WindowRect(0, 0, 1001, 800),
            marginPx = 0,
            gapPx = 1,
            sideBySide = true,
        )

        val (left, right) = halves!!
        assertEquals(500, left.width)
        assertEquals(500, right.width)
        assertTrue(left.right < right.left)
    }

    @Test
    fun `returns null when gap does not fit`() {
        assertNull(
            SplitBoundsCalculator.halves(
                display = WindowRect(0, 0, 10, 100),
                marginPx = 2,
                gapPx = 20,
                sideBySide = true,
            ),
        )
    }

    @Test
    fun `returns null for invalid display`() {
        assertNull(
            SplitBoundsCalculator.halves(
                display = WindowRect(0, 0, 0, 0),
                marginPx = 0,
                gapPx = 0,
                sideBySide = true,
            ),
        )
    }

    @Test
    fun `margin removes space from both sides`() {
        val halves = SplitBoundsCalculator.halves(
            display = WindowRect(0, 0, 1000, 1000),
            marginPx = 20,
            gapPx = 0,
            sideBySide = true,
        )!!
        assertEquals(20, halves.first.left)
        assertEquals(980, halves.second.right)
    }
}
