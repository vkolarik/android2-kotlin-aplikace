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
class UITestStatisticsScreen {

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
        launchStatisticsScreen()

        with(composeRule) {
            // Assert that the title of the screen is displayed
            onNodeWithText(composeRule.activity.getString(R.string.statistics))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test2_statistics_cards_are_displayed() {
        launchStatisticsScreen()

        with(composeRule) {
            // Assert that key statistics cards are displayed
            onNodeWithText(composeRule.activity.getString(R.string.average_trip_cost))
                .assertIsDisplayed()
            onNodeWithText(composeRule.activity.getString(R.string.fuel_costs))
                .assertIsDisplayed()
            onNodeWithText(composeRule.activity.getString(R.string.service_and_insurance_costs))
                .assertIsDisplayed()
            onNodeWithText(composeRule.activity.getString(R.string.depreciation))
                .assertIsDisplayed()
            onNodeWithText(composeRule.activity.getString(R.string.annual_mileage))
                .assertIsDisplayed()
            onNodeWithText(composeRule.activity.getString(R.string.annual_maintenance_costs))
                .assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun launchStatisticsScreen() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Destination.StatisticsScreen.route
                )
            }
        }
    }
}
