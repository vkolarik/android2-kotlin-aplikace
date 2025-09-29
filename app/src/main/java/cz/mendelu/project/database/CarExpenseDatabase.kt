package cz.mendelu.project.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cz.mendelu.project.model.Expense
import cz.mendelu.project.model.MLKitTask
import cz.mendelu.project.model.OdometerEntry
import cz.mendelu.project.model.Routine
import cz.mendelu.project.model.Settings

@Database(
    entities = [MLKitTask::class, Routine::class, Settings::class, OdometerEntry::class, Expense::class],
    version = 1,
    exportSchema = true
)
abstract class CarExpenseDatabase : RoomDatabase() {

    abstract fun carExpenseDao(): CarExpenseDao

    companion object {
        private var INSTANCE: CarExpenseDatabase? = null
        fun getDatabase(context: Context): CarExpenseDatabase {
            if (INSTANCE == null) {
                synchronized(CarExpenseDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            CarExpenseDatabase::class.java, "car_expense_database"
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}