package cz.mendelu.project.ui.screens.summary

import cz.mendelu.project.ui.screens.payment.PaymentScreenData

interface SummaryScreenActions {
    // First run

    fun insertSampleData()

    fun endFirstRun()

    // Summary

    fun onOdometerEntryChanged(data: SummaryScreenData)

    fun insertOdometerEntry()

    fun dispatchMLKitTask(description: String)
}