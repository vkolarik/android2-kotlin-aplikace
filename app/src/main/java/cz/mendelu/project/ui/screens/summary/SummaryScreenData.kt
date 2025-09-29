package cz.mendelu.project.ui.screens.summary

import cz.mendelu.project.model.OdometerEntry
import cz.mendelu.project.model.Statistics

class SummaryScreenData {
    var errors: SummaryScreenErrors = SummaryScreenErrors()
    var isFirstRun : Boolean = false

    var statistics: Statistics? = null

    var allOdometerEntries: List<OdometerEntry> = listOf()
    var odometerEntry : OdometerEntry? = null
    var odometerEntryString: String = ""
    var odometerStatusMessage : String = ""

    var mlKitTaskId : Long? = null

}