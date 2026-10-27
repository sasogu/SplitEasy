package es.sasogu.spliteasy.data

import es.sasogu.spliteasy.domain.AppPair
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PairCodecTest {
    private val pair = AppPair(
        id = "b7c3",
        packageA = "com.android.chrome",
        labelA = "Chrome",
        packageB = "com.google.android.keep",
        labelB = "Notas ✓",
        createdAt = 1_700_000_000_000L,
    )

    @Test
    fun `round trip keeps every field`() {
        val decoded = PairCodec.decode(PairCodec.encode(listOf(pair)))
        assertEquals(listOf(pair), decoded)
    }

    @Test
    fun `supports unicode and tabs in labels`() {
        val tricky = pair.copy(labelA = "Correu\t+ calendari", labelB = "Música 🎵")
        val decoded = PairCodec.decode(PairCodec.encode(listOf(tricky)))
        assertEquals(listOf(tricky), decoded)
    }

    @Test
    fun `empty input returns empty list`() {
        assertTrue(PairCodec.decode(null).isEmpty())
        assertTrue(PairCodec.decode("").isEmpty())
        assertTrue(PairCodec.decode("   ").isEmpty())
    }

    @Test
    fun `unknown header returns empty list`() {
        assertTrue(PairCodec.decode("OtherFormat/9\nfoo\tbar").isEmpty())
    }

    @Test
    fun `malformed lines are skipped`() {
        val raw = "SplitEasyFavorites/1\n" +
            "only-one-field\n" +
            "id\tnot-base64!!\tlabelA\tp2\tlabelB\t123\n" +
            "valid\t${b64("a.b")}\t${b64("A")}\t${b64("c.d")}\t${b64("C")}\t10\n"

        val decoded = PairCodec.decode(raw)
        assertEquals(1, decoded.size)
        assertEquals("a.b", decoded.single().packageA)
    }

    @Test
    fun `entries without packages are discarded`() {
        val raw = "SplitEasyFavorites/1\n" +
            "id\t\t\t\tlabel\t10\n"
        assertTrue(PairCodec.decode(raw).isEmpty())
    }

    private fun b64(value: String): String =
        java.util.Base64.getUrlEncoder().withoutPadding()
            .encodeToString(value.toByteArray(Charsets.UTF_8))
}
