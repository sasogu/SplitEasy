package es.sasogu.spliteasy.domain

/**
 * Una aplicación que puede iniciarse desde el lanzador.
 *
 * [activityName] puede ser nulo cuando la aplicación se ha recuperado de una
 * pareja guardada y ya no está instalada: en ese caso solo se conserva la
 * etiqueta para poder mostrar la pareja en la lista de favoritos.
 */
data class AppEntry(
    val packageName: String,
    val activityName: String?,
    val label: String,
    val isSystemApp: Boolean = false,
) {
    val stableKey: String get() = packageName
}
