package es.sasogu.spliteasy.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import es.sasogu.spliteasy.domain.AppEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale

/**
 * Lista de aplicaciones con actividad de lanzador.
 *
 * El manifiesto declara una consulta `<queries>` para MAIN/LAUNCHER, por lo que
 * no hace falta QUERY_ALL_PACKAGES. Android 11+ devuelve únicamente las
 * aplicaciones visibles para esa firma de intent.
 */
class InstalledAppsRepository(private val context: Context) {
    private val packageManager: PackageManager = context.packageManager

    suspend fun loadLaunchableApps(): List<AppEntry> = withContext(Dispatchers.IO) {
        val mainIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val resolved = queryLaunchActivities(mainIntent)
        val collator = Collator.getInstance(Locale.getDefault()).apply {
            strength = Collator.PRIMARY
        }

        resolved
            .filter { it.activityInfo.packageName != context.packageName }
            .groupBy { it.activityInfo.packageName }
            .mapNotNull { (packageName, activities) ->
                toEntry(packageName, activities)
            }
            .sortedWith { first, second -> collator.compare(first.label, second.label) }
    }

    fun launchIntentFor(app: AppEntry): Intent? {
        val explicit = app.activityName?.let { activityName ->
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(app.packageName, activityName)
            }
        }
        val intent = explicit ?: packageManager.getLaunchIntentForPackage(app.packageName)
        return intent?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    }

    fun isInstalled(packageName: String): Boolean =
        runCatching { packageManager.getPackageInfo(packageName, 0) }.isSuccess

    private fun toEntry(packageName: String, activities: List<ResolveInfo>): AppEntry? {
        val launchActivityName = packageManager.getLaunchIntentForPackage(packageName)
            ?.component
            ?.className

        val activity = activities.firstOrNull {
            it.activityInfo.name == launchActivityName
        } ?: activities.firstOrNull() ?: return null

        val label = activity.loadLabel(packageManager)
            .toString()
            .trim()
            .ifEmpty { packageName }

        val applicationInfo = activity.activityInfo.applicationInfo
        val isSystem = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        return AppEntry(
            packageName = packageName,
            activityName = activity.activityInfo.name,
            label = label,
            isSystemApp = isSystem,
        )
    }

    @Suppress("DEPRECATION")
    private fun queryLaunchActivities(intent: Intent): List<ResolveInfo> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(0L),
            )
        } else {
            packageManager.queryIntentActivities(intent, 0)
        }
}
