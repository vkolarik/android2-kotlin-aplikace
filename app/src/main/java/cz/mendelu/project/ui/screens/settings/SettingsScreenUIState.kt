package cz.mendelu.project.ui.screens.settings

sealed class SettingsScreenUIState {
    object Loading : SettingsScreenUIState()
    class DataChanged(val data: SettingsScreenData) : SettingsScreenUIState()
    object SettingsSaved : SettingsScreenUIState()
}