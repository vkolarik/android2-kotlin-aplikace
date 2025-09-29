package cz.mendelu.project.utils

import android.icu.util.Calendar
import android.util.Log
import cz.mendelu.project.constants.Constants
import java.text.SimpleDateFormat
import java.util.Locale

class Time {
    companion object {

        fun currentTime(): Long {
            return System.currentTimeMillis()
        }

        fun getStartOfDay(timeInMillis: Long): Long {
            val calendar = Calendar.getInstance().apply {
                this.timeInMillis = timeInMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }

        fun getEndOfDay(timeInMillis: Long): Long {
            val calendar = Calendar.getInstance().apply {
                this.timeInMillis = timeInMillis
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            return calendar.timeInMillis
        }

        fun convertMillisToDateString(timeInMillis: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis

            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-indexed in Calendar
            val year = calendar.get(Calendar.YEAR)

            return "$dayOfMonth.$month.$year"
        }

        fun getLast7DaysDates(locale: Locale): List<DayInfo> {
            val dateFormat = SimpleDateFormat("d.M.yyyy", Locale("cs", "CZ"))
            val dayNameFormat = SimpleDateFormat("EEEE", locale)
            val calendar = Calendar.getInstance()

            return (0..6).map {
                // Set to the start of the day (00:00:00)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDayMillis = calendar.timeInMillis

                // Set to the end of the day (23:59:59)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endOfDayMillis = calendar.timeInMillis

                // Get the day name and formatted date
                val dayName = dayNameFormat.format(calendar.time)
                val formattedDate = dateFormat.format(calendar.time)

                // Move to the previous day
                calendar.add(Calendar.DAY_OF_YEAR, -1)

                DayInfo(dayName, formattedDate, startOfDayMillis, endOfDayMillis)
            }
        }

        fun numberOfDaysBetweenTwoTimes(start: Long, end: Long) : Int{
            val result  = ((getStartOfDay(start) - getStartOfDay(end)) / Constants.ONE_DAY_MILLIS)
            Log.i("planty", "start $start end $end result $result")
            return result.toInt()
        }
    }


}

data class DayInfo(
    val dayName: String,
    val formattedDate: String,
    val startOfDayMillis: Long,
    val endOfDayMillis: Long
)