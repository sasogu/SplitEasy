package es.sasogu.spliteasy.domain

/**
 * Pareja de aplicaciones guardada localmente.
 *
 * Se guardan también las etiquetas para que la pareja siga siendo legible si
 * una de las aplicaciones se desinstala. El identificador es estable y se usa
 * como extra del acceso directo del lanzador.
 */
data class AppPair(
    val id: String,
    val packageA: String,
    val labelA: String,
    val packageB: String,
    val labelB: String,
    val createdAt: Long,
) {
    val displayName: String get() = "$labelA + $labelB"

    fun contains(packageName: String): Boolean =
        packageName == packageA || packageName == packageB

    fun matches(packageA: String, packageB: String): Boolean =
        (this.packageA == packageA && this.packageB == packageB) ||
            (this.packageA == packageB && this.packageB == packageA)
}
