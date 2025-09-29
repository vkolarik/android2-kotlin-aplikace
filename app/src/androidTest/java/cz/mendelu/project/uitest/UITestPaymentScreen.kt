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
class UITestPaymentScreen {

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
        launchPaymentScreen()

        with(composeRule) {
            // Assert that the title of the screen is displayed
            onNodeWithText(composeRule.activity.getString(R.string.split_journey_cost))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test2_distance_input_is_displayed() {
        launchPaymentScreen()

        with(composeRule) {
            // Assert that the "Distance" text field is displayed
            onNodeWithText(composeRule.activity.getString(R.string.distance_km))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test3_cost_input_is_displayed() {
        launchPaymentScreen()

        with(composeRule) {
            // Assert that the "Cost" text field is displayed
            onNodeWithText(composeRule.activity.getString(R.string.cost_per_kilometer_czk))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test4_split_selector_is_displayed() {
        launchPaymentScreen()

        with(composeRule) {
            // Assert that the split selector (manual, half, full) is displayed
            onNodeWithText(composeRule.activity.getString(R.string.split_factor))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test5_manual_split_input_is_displayed() {
        launchPaymentScreen()

        with(composeRule) {
            // Assert that the manual split input is displayed when "Enter" option is selected
            onNodeWithText(composeRule.activity.getString(R.string.how_many_people_are_splitting_the_cost))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test6_calculated_amounts_are_displayed() {
        launchPaymentScreen()

        with(composeRule) {
            // Assert that the calculated total amount and split amount are displayed
            onNodeWithText(composeRule.activity.getString(R.string.full_amount))
                .assertIsDisplayed()
            onNodeWithText(composeRule.activity.getString(R.string.split_amount))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test7_save_button_click() {
        launchPaymentScreen()

        with(composeRule) {
            // Here, you can assert the save button functionality by interacting with other UI elements
            // like entering data into text fields. Since the actual save button is not displayed in the code,
            // we simulate completing data entry instead of clicking a save button here.
            onNodeWithText(composeRule.activity.getString(R.string.distance_km))
                .performClick()
            // Add any further actions and checks after this
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun launchPaymentScreen() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Destination.PaymentScreen.route
                )
            }
        }
    }
}