package es.sasogu.spliteasy

import android.app.Application
import android.content.Context
import es.sasogu.spliteasy.data.AppIconCache
import es.sasogu.spliteasy.data.CapabilityRepository
import es.sasogu.spliteasy.data.FavoritesRepository
import es.sasogu.spliteasy.data.InstalledAppsRepository
import es.sasogu.spliteasy.data.SplitLaunchController

class SplitEasyApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/** Contenedor manual: la aplicación es pequeña y no necesita un framework de inyección. */
class AppContainer(context: Context) {
    val apps = InstalledAppsRepository(context)
    val icons = AppIconCache(context)
    val favorites = FavoritesRepository(context)
    val capability = CapabilityRepository(context)
    val launcher = SplitLaunchController(context)
}
