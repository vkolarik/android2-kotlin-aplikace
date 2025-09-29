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
class UITestSettingsScreen {

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
        launchSettingsScreen()

        with(composeRule) {
            // Assert that the title of the screen is displayed
            onNodeWithText(composeRule.activity.getString(R.string.settings))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test2_car_model_field_is_displayed() {
        launchSettingsScreen()

        with(composeRule) {
            // Assert that the car model text field is displayed
            onNodeWithText(composeRule.activity.getString(R.string.car_model))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test3_fuel_type_segmented_button_is_displayed() {
        launchSettingsScreen()

        with(composeRule) {
            // Assert that the fuel type segmented button is displayed
            onNodeWithText(composeRule.activity.getString(R.string.fuel_type))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test4_save_button_is_displayed() {
        launchSettingsScreen()

        with(composeRule) {
            // Assert that the save button is displayed
            onNodeWithText(composeRule.activity.getString(R.string.save))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test5_save_button_click() {
        launchSettingsScreen()

        with(composeRule) {
            // Simulate clicking the save button
            onNodeWithText(composeRule.activity.getString(R.string.save))
                .performClick()
            // Further validation after clicking can be added here, such as checking if settings are saved.
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun launchSettingsScreen() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Destination.SettingsScreen.route
                )
            }
        }
    }
}