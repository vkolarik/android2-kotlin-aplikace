package cz.mendelu.project.uitest

import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.mendelu.project.MainActivity
import cz.mendelu.project.R
import cz.mendelu.project.navigation.Destination
import cz.mendelu.project.navigation.NavGraph
import cz.mendelu.project.ui.screens.expenses.addedit.ExpensesAddEditScreen
import cz.mendelu.project.ui.screens.summary.TestTagSummaryScreenMainLayout
import cz.mendelu.project.ui.screens.summary.TestTagSummaryScreenStatisticsDetails
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@ExperimentalCoroutinesApi
@HiltAndroidTest
class UITestExpensesScreen {

    private lateinit var navController: NavHostController

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test1_screen_is_displayed() {
        launchExpensesAddEditScreen()

        with(composeRule) {
            // Assert that the title of the screen is displayed
            onNodeWithText(composeRule.activity.getString(R.string.add_expense))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test2_expense_type_dropdown_is_displayed() {
        launchExpensesAddEditScreen()

        with(composeRule) {
            // Assert that the expense type dropdown is displayed
            onNodeWithText(composeRule.activity.getString(R.string.expense_type))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test3_mileage_field_is_displayed_when_fuel_is_selected() {
        launchExpensesAddEditScreen()

        with(composeRule) {
            // Initially the mileage field should not be displayed
            onNodeWithText(composeRule.activity.getString(R.string.mileage))
                .assertDoesNotExist()

            // Simulate selecting "Fuel" expense type
            onNodeWithText(composeRule.activity.getString(R.string.expense_type))
                .performClick() // Open the dropdown
            onNodeWithText(composeRule.activity.getString(R.string.fuel))
                .performClick() // Select fuel

            // Assert that the mileage field is now displayed
            onNodeWithText(composeRule.activity.getString(R.string.mileage))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test4_start_date_field_is_displayed() {
        launchExpensesAddEditScreen()

        with(composeRule) {
            // Assert that the start date field is displayed
            onNodeWithText(composeRule.activity.getString(R.string.start_date))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test5_save_button_is_displayed() {
        launchExpensesAddEditScreen()

        with(composeRule) {
            // Assert that the save button is displayed
            onNodeWithText(composeRule.activity.getString(R.string.save))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test6_save_button_click() {
        launchExpensesAddEditScreen()

        with(composeRule) {
            // Simulate clicking the save button
            onNodeWithText(composeRule.activity.getString(R.string.save))
                .performClick()
            // Further validation after clicking can be added here, such as checking if expense is saved.
        }
    }

    @Test
    fun test7_mlkit_button_is_displayed() {
        launchExpensesAddEditScreen()

        with(composeRule) {
            // Assert that the MLKit button is displayed
            onNodeWithText(composeRule.activity.getString(R.string.read_from_receipt))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test8_mlkit_button_click() {
        launchExpensesAddEditScreen()

        with(composeRule) {
            // Simulate clicking the MLKit button
            onNodeWithText(composeRule.activity.getString(R.string.read_from_receipt))
                .performClick()
            // Further validation after clicking can be added here, such as checking if MLKit screen is navigated to.
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun launchExpensesAddEditScreen() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Destination.ExpensesAddEditScreen.route
                )
            }
        }
    }
}