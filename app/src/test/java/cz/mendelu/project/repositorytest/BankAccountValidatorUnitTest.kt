package cz.mendelu.project.repositorytest

import cz.mendelu.project.extensions.isValidBankAccountNumber
import org.junit.Test
import org.junit.Assert.*

class BankAccountValidatorUnitTest {

    @Test
    fun testValidBankAccountNumber() {
        // Valid cases
        assertTrue("123456-1234567890/0300".isValidBankAccountNumber())  // Prefix + main number + bank code
        assertTrue("1234-123456789/0300".isValidBankAccountNumber())     // Prefix + main number + bank code
        assertTrue("1234567890/0300".isValidBankAccountNumber())         // Main number + bank code
        assertTrue("123-1234567890/0300".isValidBankAccountNumber())     // Prefix + main number + bank code
    }

    @Test
    fun testInvalidBankAccountNumber() {
        // Invalid cases
        assertFalse("123456-1234567890/300".isValidBankAccountNumber()) // Invalid bank code (not 4 digits)
        assertFalse("123456-1234567890/03".isValidBankAccountNumber())  // Invalid bank code (too short)
        assertFalse("123456-1234567890/30000".isValidBankAccountNumber()) // Invalid bank code (too long)
        assertFalse("123456-1234567890".isValidBankAccountNumber())     // Missing bank code
        assertFalse("123456-1234567890/XYZ".isValidBankAccountNumber()) // Non-numeric bank code
        assertFalse("123456-XYZ/0300".isValidBankAccountNumber())       // Non-numeric main number
    }

    @Test
    fun testEmptyString() {
        // Test for empty string
        assertFalse("".isValidBankAccountNumber())
    }

    @Test
    fun testMalformedInput() {
        // Test for malformed input
        assertFalse("123456-1234567890/".isValidBankAccountNumber())    // Missing bank code after slash
        assertFalse("-1234567890/0300".isValidBankAccountNumber())      // Leading dash without prefix
    }
}