package cz.mendelu.project.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "mlkit_tasks")
data class MLKitTask(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var returnValue: String? = null,
    var description: String? = null,
    var completed: Boolean? = false,
)