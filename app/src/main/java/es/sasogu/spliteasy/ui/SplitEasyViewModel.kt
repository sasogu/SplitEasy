package es.sasogu.spliteasy.ui

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.sasogu.spliteasy.R
import es.sasogu.spliteasy.SplitEasyApp
import es.sasogu.spliteasy.data.ShortcutHelper
import es.sasogu.spliteasy.domain.AppEntry
import es.sasogu.spliteasy.domain.AppPair
import es.sasogu.spliteasy.domain.CapabilityEvaluator
import es.sasogu.spliteasy.domain.CapabilityReport
import es.sasogu.spliteasy.domain.DeviceFacts
import es.sasogu.spliteasy.domain.GuideVendor
import es.sasogu.spliteasy.domain.NavigationMode
import es.sasogu.spliteasy.domain.NavigationModeEstimator
import es.sasogu.spliteasy.domain.VendorDetector
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class Route { HOME, PICKER, RUN, HELP }

enum class Slot {
    A,
    B,
    ;

    fun other(): Slot = if (this == A) B else A
}

enum class RunPhase {
    READY,
    ATTEMPTING,
    ASK,
    SUCCESS,
    MANUAL,
}

data class UiMessage(val text: String)

data class UiState(
    val route: Route = Route.HOME,
    val loadingApps: Boolean = true,
    val apps: List<AppEntry> = emptyList(),
    val installedPackages: Set<String> = emptySet(),
    val query: String = "",
    val pickerSlot: Slot = Slot.A,
    val slotA: AppEntry? = null,
    val slotB: AppEntry? = null,
    val favorites: List<AppPair> = emptyList(),
    val facts: DeviceFacts? = null,
    val capability: CapabilityReport? = null,
    val navigationMode: NavigationMode = NavigationMode.UNKNOWN,
    val navigationIsOverride: Boolean = false,
    val vendor: GuideVendor = GuideVendor.GENERIC,
    val runPhase: RunPhase = RunPhase.READY,
    val lastAttemptWasExperimental: Boolean = false,
    val experimentalEnabled: Boolean = false,
) {
    val canOpenBoth: Boolean
        get() = slotA != null && slotB != null && slotA.packageName != slotB.packageName

    val pairSaved: Boolean
        get() {
            val a = slotA ?: return false
            val b = slotB ?: return false
            return favorites.any { it.matches(a.packageName, b.packageName) }
        }

    val pairHasMissingApp: Boolean
        get() = listOfNotNull(slotA, slotB).any {
            !loadingApps && it.packageName !in installedPackages
        }
}

class SplitEasyViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as SplitEasyApp).container

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<UiMessage>(extraBufferCapacity = 8)
    val events: SharedFlow<UiMessage> = _events.asSharedFlow()

    init {
        val facts = container.capability.deviceFacts()
        _state.update {
            it.copy(
                facts = facts,
                capability = CapabilityEvaluator.evaluate(facts, container.capability.evidence()),
                vendor = VendorDetector.detect(facts.manufacturer, facts.brand),
                experimentalEnabled = container.capability.evidence().experimentalAdjacentEnabled,
            )
        }
        loadApps()
        viewModelScope.launch {
            container.favorites.favorites.collect { favorites ->
                _state.update { it.copy(favorites = favorites) }
            }
        }
    }

    fun loadApps() {
        viewModelScope.launch {
            val apps = runCatching { container.apps.loadLaunchableApps() }
                .getOrDefault(emptyList())
            val installed = apps.mapTo(mutableSetOf()) { it.packageName }
            _state.update { current ->
                current.copy(
                    loadingApps = false,
                    apps = apps,
                    installedPackages = installed,
                    slotA = refreshSelection(current.slotA, apps),
                    slotB = refreshSelection(current.slotB, apps),
                )
            }
        }
    }

    fun updateNavigationInsets(bottomDp: Int, leftDp: Int, rightDp: Int) {
        val current = _state.value
        val facts = (current.facts ?: container.capability.deviceFacts()).copy(
            navigationBottomInsetDp = bottomDp,
            navigationLeftInsetDp = leftDp,
            navigationRightInsetDp = rightDp,
        )
        val override = container.capability.navigationOverride()
        val mode = override ?: NavigationModeEstimator.estimate(bottomDp, leftDp, rightDp)
        _state.update {
            it.copy(
                facts = facts,
                navigationMode = mode,
                navigationIsOverride = override != null,
            )
        }
    }

    fun setNavigationMode(mode: NavigationMode) {
        container.capability.setNavigationOverride(mode)
        _state.update {
            it.copy(navigationMode = mode, navigationIsOverride = true)
        }
    }

    fun openPicker(slot: Slot) {
        _state.update {
            it.copy(route = Route.PICKER, pickerSlot = slot, query = "")
        }
    }

    fun updateQuery(query: String) {
        _state.update { it.copy(query = query) }
    }

    fun selectApp(app: AppEntry) {
        val current = _state.value
        val targetSlot = current.pickerSlot
        val other = if (targetSlot == Slot.A) current.slotB else current.slotA

        if (other?.packageName == app.packageName) {
            message(R.string.picker_same_app)
            return
        }

        val newA = if (targetSlot == Slot.A) app else current.slotA
        val newB = if (targetSlot == Slot.B) app else current.slotB
        val otherIsEmpty = if (targetSlot == Slot.A) newB == null else newA == null

        _state.update {
            it.copy(
                slotA = newA,
                slotB = newB,
                route = if (otherIsEmpty) Route.PICKER else Route.HOME,
                pickerSlot = if (otherIsEmpty) targetSlot.other() else targetSlot,
                query = "",
            )
        }
    }

    fun clearSlot(slot: Slot) {
        _state.update {
            if (slot == Slot.A) it.copy(slotA = null) else it.copy(slotB = null)
        }
    }

    fun swapSlots() {
        _state.update { it.copy(slotA = it.slotB, slotB = it.slotA) }
    }

    fun openBoth() {
        val current = _state.value
        if (!current.canOpenBoth) {
            message(R.string.home_choose_both)
            return
        }
        _state.update {
            it.copy(
                route = Route.RUN,
                runPhase = RunPhase.READY,
                lastAttemptWasExperimental = false,
            )
        }
    }

    fun showManualSteps() {
        _state.update { it.copy(runPhase = RunPhase.MANUAL) }
    }

    fun attemptAutomatic(experimental: Boolean = false) {
        val current = _state.value
        val first = current.slotA ?: return
        val second = current.slotB ?: return

        _state.update {
            it.copy(
                runPhase = RunPhase.ATTEMPTING,
                lastAttemptWasExperimental = experimental,
            )
        }

        viewModelScope.launch {
            val started = if (experimental) {
                container.launcher.launchAdjacent(first, second)
            } else {
                container.launcher.launchFreeform(first, second)
            }

            if (!started) {
                _state.update { it.copy(runPhase = RunPhase.MANUAL) }
                message(R.string.home_launch_failed)
                return@launch
            }

            delay(ATTEMPT_RESULT_DELAY_MS)
            _state.update { it.copy(runPhase = RunPhase.ASK) }
        }
    }

    fun confirmAutomaticSuccess() {
        val wasExperimental = _state.value.lastAttemptWasExperimental
        if (wasExperimental) {
            container.capability.markSplitConfirmed()
        } else {
            container.capability.recordFreeformSuccess()
        }
        _state.update { it.copy(runPhase = RunPhase.SUCCESS) }
        refreshCapability()
        message(R.string.run_auto_success)
    }

    fun reportAutomaticFailure() {
        val wasExperimental = _state.value.lastAttemptWasExperimental
        if (wasExperimental) {
            message(R.string.run_experimental_failed)
        } else {
            container.capability.recordFreeformFailure()
        }
        _state.update { it.copy(runPhase = RunPhase.MANUAL) }
        refreshCapability()
    }

    fun markManualSuccess() {
        container.capability.markSplitConfirmed()
        _state.update { it.copy(runPhase = RunPhase.SUCCESS) }
        refreshCapability()
        message(R.string.run_manual_done_snackbar)
    }

    fun onSplitConfirmedBySystem() {
        container.capability.markSplitConfirmed()
        refreshCapability()
    }

    fun prepareManualLaunch() {
        val current = _state.value
        val first = current.slotA ?: return
        val second = current.slotB ?: return
        if (!container.launcher.launchSequential(first, second)) {
            message(R.string.home_launch_failed)
        }
    }

    fun launchSingle(slot: Slot) {
        val app = if (slot == Slot.A) _state.value.slotA else _state.value.slotB
        if (app == null || !container.launcher.launch(app)) {
            message(R.string.home_launch_failed)
        }
    }

    fun saveCurrentPair() {
        val selection = currentSelectionOrNull() ?: return
        val existing = container.favorites.favorites.value.firstOrNull {
            it.matches(selection.first.packageName, selection.second.packageName)
        }
        if (existing != null) {
            message(R.string.home_pair_already_saved)
            return
        }
        val pair = newPair(selection.first, selection.second)
        if (container.favorites.add(pair)) {
            message(R.string.home_pair_saved)
        } else {
            message(R.string.home_pair_already_saved)
        }
    }

    fun pinCurrentPair() {
        val selection = currentSelectionOrNull() ?: return
        val existing = container.favorites.favorites.value.firstOrNull {
            it.matches(selection.first.packageName, selection.second.packageName)
        }
        val pair = existing ?: newPair(selection.first, selection.second)
        if (existing == null && !container.favorites.add(pair)) return
        pinPair(pair)
    }

    fun openFavorite(pair: AppPair) {
        val current = _state.value
        val first = resolvePairApp(pair.packageA, pair.labelA, current.apps)
        val second = resolvePairApp(pair.packageB, pair.labelB, current.apps)
        _state.update {
            it.copy(
                slotA = first,
                slotB = second,
                route = Route.RUN,
                runPhase = RunPhase.READY,
                lastAttemptWasExperimental = false,
            )
        }
    }

    fun loadFavorite(pair: AppPair) {
        val current = _state.value
        _state.update {
            it.copy(
                slotA = resolvePairApp(pair.packageA, pair.labelA, current.apps),
                slotB = resolvePairApp(pair.packageB, pair.labelB, current.apps),
                route = Route.HOME,
            )
        }
        message(R.string.home_favorite_loaded)
    }

    fun openPairById(pairId: String) {
        val pair = container.favorites.find(pairId)
        if (pair == null) {
            message(R.string.home_favorite_not_found)
            return
        }
        openFavorite(pair)
    }

    fun deleteFavorite(pair: AppPair) {
        container.favorites.remove(pair.id)
        message(R.string.home_favorite_deleted)
    }

    fun pinFavorite(pair: AppPair) {
        pinPair(pair)
    }

    fun setExperimentalEnabled(enabled: Boolean) {
        container.capability.setExperimentalAdjacentEnabled(enabled)
        _state.update { it.copy(experimentalEnabled = enabled) }
        refreshCapability()
    }

    fun resetCapability() {
        container.capability.resetEvidence()
        refreshCapability()
        message(R.string.help_advanced_reset_done)
    }

    fun backToHome() {
        _state.update { it.copy(route = Route.HOME) }
    }

    fun goToHelp() {
        _state.update { it.copy(route = Route.HELP) }
    }

    private fun currentSelectionOrNull(): Pair<AppEntry, AppEntry>? {
        val current = _state.value
        val first = current.slotA
        val second = current.slotB
        if (first == null || second == null) {
            message(R.string.home_save_needs_two)
            return null
        }
        if (first.packageName == second.packageName) {
            message(R.string.home_same_app)
            return null
        }
        return first to second
    }

    private fun newPair(first: AppEntry, second: AppEntry): AppPair = AppPair(
        id = UUID.randomUUID().toString(),
        packageA = first.packageName,
        labelA = first.label,
        packageB = second.packageName,
        labelB = second.label,
        createdAt = System.currentTimeMillis(),
    )

    private fun pinPair(pair: AppPair) {
        when (ShortcutHelper.pin(getApplication(), pair)) {
            ShortcutHelper.PinResult.Requested -> message(R.string.home_favorite_pin_requested)
            ShortcutHelper.PinResult.Unsupported -> message(R.string.home_favorite_pin_unsupported)
            ShortcutHelper.PinResult.Failed -> message(R.string.home_favorite_pin_failed)
        }
    }

    private fun refreshCapability() {
        val facts = _state.value.facts ?: container.capability.deviceFacts()
        val evidence = container.capability.evidence()
        val report = CapabilityEvaluator.evaluate(facts, evidence)
        _state.update {
            it.copy(
                capability = report,
                experimentalEnabled = evidence.experimentalAdjacentEnabled,
            )
        }
    }

    private fun resolvePairApp(
        packageName: String,
        label: String,
        apps: List<AppEntry>,
    ): AppEntry = apps.firstOrNull { it.packageName == packageName }
        ?: AppEntry(packageName = packageName, activityName = null, label = label)

    private fun refreshSelection(selection: AppEntry?, apps: List<AppEntry>): AppEntry? =
        selection?.let { selected ->
            apps.firstOrNull { it.packageName == selected.packageName }
        }

    private fun message(@StringRes resourceId: Int, vararg arguments: Any) {
        _events.tryEmit(
            UiMessage(getApplication<Application>().getString(resourceId, *arguments)),
        )
    }

    private companion object {
        const val ATTEMPT_RESULT_DELAY_MS = 1_400L
    }
}
