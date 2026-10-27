package es.sasogu.spliteasy.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationModeEstimatorTest {
    @Test
    fun `48dp bottom inset is three button`() {
        assertEquals(
            NavigationMode.THREE_BUTTON,
            NavigationModeEstimator.estimate(bottomInsetDp = 48, leftInsetDp = 0, rightInsetDp = 0),
        )
    }

    @Test
    fun `24dp bottom inset is gesture`() {
        assertEquals(
            NavigationMode.GESTURES,
            NavigationModeEstimator.estimate(bottomInsetDp = 24, leftInsetDp = 0, rightInsetDp = 0),
        )
    }

    @Test
    fun `side inset in landscape is three button`() {
        assertEquals(
            NavigationMode.THREE_BUTTON,
            NavigationModeEstimator.estimate(bottomInsetDp = 0, leftInsetDp = 48, rightInsetDp = 0),
        )
    }

    @Test
    fun `no insets is unknown`() {
        assertEquals(
            NavigationMode.UNKNOWN,
            NavigationModeEstimator.estimate(bottomInsetDp = 0, leftInsetDp = 0, rightInsetDp = 0),
        )
    }

    @Test
    fun `threshold boundaries`() {
        assertEquals(
            NavigationMode.GESTURES,
            NavigationModeEstimator.estimate(bottomInsetDp = 39, leftInsetDp = 0, rightInsetDp = 0),
        )
        assertEquals(
            NavigationMode.THREE_BUTTON,
            NavigationModeEstimator.estimate(bottomInsetDp = 40, leftInsetDp = 0, rightInsetDp = 0),
        )
    }
}
