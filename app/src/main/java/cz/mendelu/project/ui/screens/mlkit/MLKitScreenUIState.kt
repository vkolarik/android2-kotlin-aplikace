package cz.mendelu.project.ui.screens.mlkit

sealed class MLKitScreenUIState {
    class Loading : MLKitScreenUIState()
    class ResultSaved : MLKitScreenUIState()
    class DataChanged(val data: MLKitScreenData) : MLKitScreenUIState()

}
