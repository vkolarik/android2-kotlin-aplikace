package cz.mendelu.project.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Double.toPercentage(): String{
    val value = (this * 100.0).round()
    return "${value}%"
}

fun Double.round(): String {
    return String.format("%.2f", this)
}

fun Double.formatNumber(): String {
    return String.format("%,.2f", this)
        .replace(",", " ", true)
        .replace(".", ",")// Replace thousands separator with space
}

fun Int.formatWithSpaces(): String {
    return toString()
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()
}

fun Long.formatWithSpaces(): String {
    return toString()
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()
}

fun Long.toFormattedDate(): String {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("d.M.yyyy"))
}