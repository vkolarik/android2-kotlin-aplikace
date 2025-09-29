package cz.mendelu.project.uitest

import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.mendelu.project.MainActivity
import cz.mendelu.project.R
import cz.mendelu.project.navigation.Destination
import cz.mendelu.project.navigation.NavGraph
import cz.mendelu.project.ui.screens.summary.TestTagSummaryScreenMainLayout
import cz.mendelu.project.ui.screens.summary.TestTagSummaryScreenOdometerInput
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
class UITestOdometerScreen {

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
        launchOdometerScreen()

        with(composeRule) {
            // Assert that the title of the screen is displayed
            onNodeWithText(composeRule.activity.getString(R.string.odometer_history))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test2_odometer_entries_are_displayed() {
        launchMainScreenWithNavigation(true)

        with(composeRule) {
            onNodeWithTag(TestTagSummaryScreenOdometerInput).performTextInput("123")
            onNodeWithText(composeRule.activity.getString(R.string.save)).performClick()
            onNodeWithText(composeRule.activity.getString(R.string.mileage_history)).performClick()
            composeRule.waitForIdle()
            onNodeWithText(composeRule.activity.getString(R.string.input_your_odometer_status_on_the_main_screen))
                .assertDoesNotExist() // Ensure the placeholder does not show if data is available
        }
    }

    @Test
    fun test3_no_entries_placeholder_is_displayed() {
        // Simulate an empty odometer data state
        launchOdometerScreen()

        with(composeRule) {
            // Assert the placeholder is displayed when no data is available
            onNodeWithText(composeRule.activity.getString(R.string.input_your_odometer_status_on_the_main_screen))
                .assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun launchOdometerScreen() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Destination.OdometerScreen.route
                )
            }
        }
    }

    private fun launchMainScreenWithNavigation(skipIntro: Boolean = false) {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Destination.SummaryScreen.route
                )
            }
        }
    }
}
