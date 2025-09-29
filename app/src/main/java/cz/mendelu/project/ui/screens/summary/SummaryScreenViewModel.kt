package cz.mendelu.project.ui.screens.summary

import android.content.Context
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.mendelu.project.R
import cz.mendelu.project.database.ILocalCarExpenseRepository
import cz.mendelu.project.datastore.IDataStoreRepository
import cz.mendelu.project.model.Expense
import cz.mendelu.project.model.ExpenseType
import cz.mendelu.project.model.MLKitTask
import cz.mendelu.project.model.OdometerEntry
import cz.mendelu.project.model.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class SummaryScreenViewModel @Inject constructor(
    private val repository: ILocalCarExpenseRepository,
    private val dataStoreRepository: IDataStoreRepository,
    @ApplicationContext private val context: Context
) : ViewModel(), SummaryScreenActions {

    private val _summaryScreenUIState: MutableStateFlow<SummaryScreenUIState> =
        MutableStateFlow(SummaryScreenUIState.Loading)
    val summaryUIState = _summaryScreenUIState.asStateFlow()

    private var data = SummaryScreenData()

    fun initialize(skipIntro: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (dataStoreRepository.getFirstRun()) {
                repository.insert(Settings(id = 1))
                dataStoreRepository.setFirstRun()
                if (!skipIntro) {
                    data.isFirstRun = true
                    _summaryScreenUIState.update {
                        SummaryScreenUIState.SummaryScreenDataChanged(data)
                    }
                } else {
                    endFirstRun()
                }
            } else {
                repository.getAllOdometerEntries().collect { entries ->
                    data.allOdometerEntries = entries

                    if (entries.isNotEmpty()) {
                        val lastEntry = entries.last()
                        data.odometerEntryString = lastEntry.mileage.toString()

                        // Calculate time differences
                        val now = System.currentTimeMillis()
                        val secondsDifference = (now - lastEntry.timestamp!!) / 1000

                        // Calculate days difference for older entries
                        val today = LocalDate.now()
                        val entryDate = Instant.ofEpochMilli(lastEntry.timestamp!!)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        val daysDifference = ChronoUnit.DAYS.between(entryDate, today)

                        data.odometerStatusMessage = when {
                            secondsDifference < 10 -> context.getString(R.string.mileage_saved)
                            daysDifference == 0L -> context.getString(R.string.odometer_updated_today)
                            daysDifference == 1L -> context.getString(R.string.odometer_updated_yesterday)
                            else -> context.getString(
                                R.string.odometer_updated_days_ago,
                                daysDifference
                            )
                        }
                    } else {
                        data.odometerEntryString = ""
                        data.odometerStatusMessage =
                            context.getString(R.string.insert_your_first_mileage_entry)
                    }

                    loadStatistics()

                    _summaryScreenUIState.update {
                        SummaryScreenUIState.SummaryScreenDataChanged(data)
                    }
                }
            }
        }
    }

    fun loadStatistics() {
        viewModelScope.launch(Dispatchers.IO) {
            data.statistics = repository.getStatistics()
        }
    }

    override fun insertSampleData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(OdometerEntry.createTodaysOdometerEntry(10000))
            repository.insert(OdometerEntry.createTodaysOdometerEntry(20000))
            repository.insert(OdometerEntry.createTodaysOdometerEntry(30000))
            repository.insert(
                Expense(
                    expenseType = ExpenseType.FUEL.typeStringId,
                    amount = 1600,
                    date = System.currentTimeMillis(),
                    fuelMileageInfo = 800
                )
            )
            repository.insert(
                Expense(
                    expenseType = ExpenseType.INSURANCE.typeStringId,
                    amount = 3200,
                    date = System.currentTimeMillis()
                )
            )
            repository.insert(
                Expense(
                    expenseType = ExpenseType.CLEANING.typeStringId,
                    amount = 300,
                    date = System.currentTimeMillis()
                )
            )

            initialize(false)
        }
    }

    override fun endFirstRun() {
        initialize(false)
    }

    override fun onOdometerEntryChanged(it: SummaryScreenData) {
        data.odometerEntryString = data.odometerEntryString.replace(Regex("[^0-9]"), "")

        data = it
        _summaryScreenUIState.update {
            SummaryScreenUIState.SummaryScreenDataChanged(data)
        }
    }

    override fun insertOdometerEntry() {
        // clear errors
        val currentErrors = SummaryScreenErrors()
        val lastEntry: OdometerEntry? = data.allOdometerEntries.lastOrNull()

        // check validity
        if (data.odometerEntryString.toLongOrNull() == null || data.odometerEntryString.toLong() == 0L || data.odometerEntryString.isBlank()) {
            currentErrors.odometerError = R.string.the_input_is_not_a_valid_number
            currentErrors.errorOccurred = true
        }

        if (lastEntry != null) {
            if (lastEntry.mileage!! >= data.odometerEntryString.toLong()) {
                currentErrors.odometerError = R.string.mileage_must_be_higher
                currentErrors.errorOccurred = true
            }
        }

        // Save if no errors
        if (!currentErrors.errorOccurred) {
            data.odometerEntry =
                OdometerEntry.createTodaysOdometerEntry(data.odometerEntryString.toLong())
            viewModelScope.launch {
                repository.insert(data.odometerEntry!!)
            }
        }

        // Pass errors to UI state
        data.errors = currentErrors

        loadStatistics()

        // send UI state change
        _summaryScreenUIState.update {
            SummaryScreenUIState.SummaryScreenDataChanged(data)
        }
    }

    override fun dispatchMLKitTask(description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val mlKitTask = MLKitTask(description = description, completed = false)
            data.mlKitTaskId = repository.insert(mlKitTask)
            onOdometerEntryChanged(data)
            _summaryScreenUIState.update {
                SummaryScreenUIState.DispatchedMLKit
            }
        }
    }

    fun checkMLKit() {
        data.mlKitTaskId?.let { taskId ->
            viewModelScope.launch(Dispatchers.IO) {
                val mlKitTask = repository.getMlKitTask(taskId)
                if (mlKitTask.completed == true) {
                    data.odometerEntryString = mlKitTask.returnValue!!
                    onOdometerEntryChanged(data)
                }
            }
        }
    }
}
