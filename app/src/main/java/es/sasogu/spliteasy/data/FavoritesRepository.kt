package es.sasogu.spliteasy.data

import android.content.Context
import es.sasogu.spliteasy.domain.AppPair
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Parejas guardadas en SharedPreferences. Todo el almacenamiento es local y la
 * copia de seguridad de Android está desactivada en el manifiesto.
 */
class FavoritesRepository(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val _favorites = MutableStateFlow(
        PairCodec.decode(preferences.getString(KEY_FAVORITES, null))
            .sortedByDescending { it.createdAt },
    )

    val favorites: StateFlow<List<AppPair>> = _favorites.asStateFlow()

    @Synchronized
    fun add(pair: AppPair): Boolean {
        val current = _favorites.value
        if (current.any { it.matches(pair.packageA, pair.packageB) }) return false
        val updated = (listOf(pair) + current).sortedByDescending { it.createdAt }
        persist(updated)
        return true
    }

    @Synchronized
    fun remove(pairId: String) {
        persist(_favorites.value.filterNot { it.id == pairId })
    }

    fun find(pairId: String): AppPair? = _favorites.value.firstOrNull { it.id == pairId }

    fun isSaved(packageA: String, packageB: String): Boolean =
        _favorites.value.any { it.matches(packageA, packageB) }

    private fun persist(pairs: List<AppPair>) {
        _favorites.value = pairs
        preferences.edit()
            .putString(KEY_FAVORITES, PairCodec.encode(pairs))
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "spliteasy_favorites"
        const val KEY_FAVORITES = "pairs"
    }
}
