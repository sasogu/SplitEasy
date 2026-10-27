package es.sasogu.spliteasy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppSearchTest {
    private val apps = listOf(
        AppEntry("com.example.musica", null, "Música"),
        AppEntry("com.example.calendario", null, "Calendario"),
        AppEntry("com.example.calculadora", null, "Calculadora"),
        AppEntry("com.example.camara", null, "Cámara"),
        AppEntry("com.example.webcam", null, "Webcam"),
    )

    @Test
    fun `ignores accents and case`() {
        val result = AppSearch.filter(apps, "MUSICA")
        assertEquals(listOf("Música"), result.map { it.label })
    }

    @Test
    fun `searches package name too`() {
        val result = AppSearch.filter(apps, "calendario")
        assertEquals(listOf("Calendario"), result.map { it.label })
    }

    @Test
    fun `matches at the start come first`() {
        val result = AppSearch.filter(apps, "cam")
        assertEquals(listOf("Cámara", "Webcam"), result.map { it.label })
    }

    @Test
    fun `empty query keeps the original list`() {
        val result = AppSearch.filter(apps, "   ")
        assertEquals(apps, result)
    }

    @Test
    fun `no match returns empty`() {
        assertTrue(AppSearch.filter(apps, "zzz").isEmpty())
    }

    @Test
    fun `normalize removes diacritics`() {
        assertEquals("aplicacion", AppSearch.normalize("Aplicación"))
    }
}
