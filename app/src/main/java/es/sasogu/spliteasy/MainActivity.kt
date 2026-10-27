package es.sasogu.spliteasy

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import es.sasogu.spliteasy.data.ShortcutHelper
import es.sasogu.spliteasy.ui.SplitEasyRoot
import es.sasogu.spliteasy.ui.SplitEasyViewModel
import es.sasogu.spliteasy.ui.theme.SplitEasyTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: SplitEasyViewModel by viewModels()
    private var currentLocale: Locale = Locale.getDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        currentLocale = resources.configuration.locales.takeIf { !it.isEmpty }?.get(0)
            ?: Locale.getDefault()
        handleShortcutIntent(intent)

        setContent {
            SplitEasyTheme {
                SplitEasyRoot(viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleShortcutIntent(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val locale = newConfig.locales.takeIf { !it.isEmpty }?.get(0)
        if (locale != null && locale != currentLocale) {
            currentLocale = locale
            viewModel.loadApps()
        }
    }

    override fun onMultiWindowModeChanged(
        isInMultiWindowMode: Boolean,
        newConfig: Configuration,
    ) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        if (isInMultiWindowMode) {
            viewModel.onSplitConfirmedBySystem()
        }
    }

    private fun handleShortcutIntent(intent: Intent?) {
        if (intent?.action != ShortcutHelper.ACTION_OPEN_PAIR) return
        intent.getStringExtra(ShortcutHelper.EXTRA_PAIR_ID)?.let(viewModel::openPairById)
        intent.action = null
        intent.removeExtra(ShortcutHelper.EXTRA_PAIR_ID)
    }
}
