package es.sasogu.spliteasy.data

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caché en memoria de iconos. Se limita a 64 entradas para no retener todas las
 * aplicaciones instaladas en memoria. Siempre se accede desde IO.
 */
class AppIconCache(private val context: Context) {
    private val cache = object : LruCache<String, Bitmap>(64) {}

    suspend fun load(packageName: String): Bitmap? = withContext(Dispatchers.IO) {
        cache.get(packageName) ?: runCatching {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            drawable.toBitmap(width = ICON_SIZE_PX, height = ICON_SIZE_PX).also {
                cache.put(packageName, it)
            }
        }.getOrNull()
    }

    private companion object {
        const val ICON_SIZE_PX = 96
    }
}
