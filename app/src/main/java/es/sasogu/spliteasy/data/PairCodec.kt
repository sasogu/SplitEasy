package es.sasogu.spliteasy.data

import es.sasogu.spliteasy.domain.AppPair
import java.util.Base64

/**
 * Formato local de las parejas guardadas.
 *
 * Es texto plano con campos Base64 URL-safe separados por tabuladores. No se
 * usa JSON para evitar dependencias y mantener el códec verificable con pruebas
 * de JVM. La cabecera permite migrar el formato en el futuro.
 */
object PairCodec {
    private const val HEADER = "SplitEasyFavorites/1"
    private const val FIELD_SEPARATOR = "\t"

    fun encode(pairs: List<AppPair>): String = buildString {
        append(HEADER).append('\n')
        for (pair in pairs) {
            append(
                listOf(
                    pair.id,
                    encodeField(pair.packageA),
                    encodeField(pair.labelA),
                    encodeField(pair.packageB),
                    encodeField(pair.labelB),
                    pair.createdAt.toString(),
                ).joinToString(FIELD_SEPARATOR),
            )
            append('\n')
        }
    }

    fun decode(raw: String?): List<AppPair> {
        if (raw.isNullOrBlank()) return emptyList()
        val lines = raw.lineSequence().filter { it.isNotBlank() }.toList()
        if (lines.firstOrNull() != HEADER) return emptyList()

        return lines.drop(1).mapNotNull { line ->
            val parts = line.split(FIELD_SEPARATOR)
            if (parts.size != 6 || parts[0].isBlank()) return@mapNotNull null
            runCatching {
                AppPair(
                    id = parts[0],
                    packageA = decodeField(parts[1]),
                    labelA = decodeField(parts[2]),
                    packageB = decodeField(parts[3]),
                    labelB = decodeField(parts[4]),
                    createdAt = parts[5].toLong(),
                )
            }.getOrNull()?.takeIf {
                it.packageA.isNotBlank() && it.packageB.isNotBlank()
            }
        }
    }

    private fun encodeField(value: String): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray(Charsets.UTF_8))

    private fun decodeField(value: String): String =
        String(Base64.getUrlDecoder().decode(value), Charsets.UTF_8)
}
