package cz.mendelu.project.model

data class Statistics(
    val costPerKmSum: Double?,
    val costPerKmFuel: Double?,
    val costPerKmDepreciation: Double?,
    val costPerKmMaintenance: Double?,
    val mileageLastYear: Int?,
    val expensesLastYear: Int?,
    val yearlyMaintenanceCost: Int?,
    val upcomingTasksCount: Int?

)