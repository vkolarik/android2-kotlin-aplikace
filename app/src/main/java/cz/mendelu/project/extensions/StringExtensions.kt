package cz.mendelu.project.extensions

import java.util.regex.Pattern

fun String.isValidBankAccountNumber(): Boolean {
    if (this.isEmpty()) {
        return false
    }

    val pattern = Pattern.compile("^([0-9]{1,6}-)?([0-9]{1,10})/([0-9]{4})$")
    val matcher = pattern.matcher(this)

    if (!matcher.matches()) {
        return false
    }

    val accountNumberPart = matcher.group(2)
    val bankCodePart = matcher.group(3)

    if (accountNumberPart.isNullOrBlank() || bankCodePart.isNullOrBlank()) {
        return false
    }

    return true
}