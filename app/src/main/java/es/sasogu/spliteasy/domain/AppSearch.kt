package es.sasogu.spliteasy.domain

import java.text.Normalizer
import java.util.Locale

/**
 * Búsqueda tolerante a mayúsculas y tildes. "musica" encuentra "Música".
 *
 * Las coincidencias al principio del nombre se muestran antes que las que solo
 * lo contienen, para que "ca" no esconda "Calendario" detrás de "Calculadora".
 */
object AppSearch {
    private val combiningMarks = Regex("\\p{M}+")

    fun normalize(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD)
            .replace(combiningMarks, "")
            .lowercase(Locale.ROOT)
            .trim()

    fun filter(apps: List<AppEntry>, query: String): List<AppEntry> {
        val normalizedQuery = normalize(query)
        if (normalizedQuery.isEmpty()) return apps

        return apps
            .filter { app ->
                normalize(app.label).contains(normalizedQuery) ||
                    normalize(app.packageName).contains(normalizedQuery)
            }
            .sortedWith(
                compareBy<AppEntry>(
                    { !normalize(it.label).startsWith(normalizedQuery) },
                    { normalize(it.label) },
                ),
            )
    }
}
