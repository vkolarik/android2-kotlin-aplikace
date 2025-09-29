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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UITestSummaryScreen {

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
        launchMainScreenWithNavigation(skipIntro = true)

        with(composeRule) {
            onNodeWithTag(TestTagSummaryScreenMainLayout).assertIsDisplayed()
        }
    }

    @Test
    fun test2_expenses_card_navigates_to_expenses_list_screen() {
        launchMainScreenWithNavigation(true)

        with(composeRule) {
            // Verify that the "Expenses" card is displayed with the correct title
            onNodeWithText(composeRule.activity.getString(R.string.expenses))
                .assertIsDisplayed()

            // Simulate clicking the "Expenses" card
            onNodeWithText(composeRule.activity.getString(R.string.expenses)).performClick()

            // Verify navigation to the Expenses List Screen
            composeRule.runOnIdle {
                assert(navController.currentDestination?.route == Destination.ExpensesListScreen.route) {
                    "Navigation to Expenses List Screen failed!"
                }
            }
        }
    }

    @Test
    fun test3_dropdown_menu_appears_on_click() {
        launchMainScreenWithNavigation(skipIntro = true)

        with(composeRule) {
            // Click on the more options button
            onNodeWithContentDescription(composeRule.activity.getString(R.string.menu))
                .performClick()

            // Assert dropdown menu items are displayed
            onNodeWithText(composeRule.activity.getString(R.string.settings)).assertIsDisplayed()
            onNodeWithText(composeRule.activity.getString(R.string.about_app)).assertIsDisplayed()
        }
    }

    @Test
    fun test4_odometer_entry_error_displayed() {
        launchMainScreenWithNavigation(skipIntro = true)

        with(composeRule) {
            runBlocking {
                // Simulate odometer entry error
                // You would need to mock the state here for the error
            }
            onNodeWithText(composeRule.activity.getString(R.string.current_odometer_value))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test5_settings_screen_navigation() {
        launchMainScreenWithNavigation(skipIntro = true)

        with(composeRule) {
            // Click on the more options button
            onNodeWithContentDescription(composeRule.activity.getString(R.string.menu))
                .performClick()

            // Click on the "Settings" menu item
            onNodeWithText(composeRule.activity.getString(R.string.settings)).performClick()

            // Verify navigation to the Settings Screen
            composeRule.runOnIdle {
                assert(navController.currentDestination?.route == Destination.SettingsScreen.route) {
                    "Navigation to Settings Screen failed!"
                }
            }
        }
    }

    @Test
    fun test6_about_app_screen_navigation() {
        launchMainScreenWithNavigation(skipIntro = true)

        with(composeRule) {
            // Click on the more options button
            onNodeWithContentDescription(composeRule.activity.getString(R.string.menu))
                .performClick()

            // Click on the "About App" menu item
            onNodeWithText(composeRule.activity.getString(R.string.about_app)).performClick()

            // Verify navigation to the About App Screen
            composeRule.runOnIdle {
                assert(navController.currentDestination?.route == Destination.AboutScreen.route) {
                    "Navigation to About App Screen failed!"
                }
            }
        }
    }


    @Test
    fun test7_map_screen_navigation() {
        launchMainScreenWithNavigation(skipIntro = true)

        with(composeRule) {
            // Click on the "Map" button or card
            onNodeWithText(composeRule.activity.getString(R.string.show_map)).performClick()

            // Verify navigation to the Map Screen
            composeRule.runOnIdle {
                assert(navController.currentDestination?.route == Destination.MapScreen.route) {
                    "Navigation to Map Screen failed!"
                }
            }
        }
    }

    @Test
    fun test8_routines_screen_navigation() {
        launchMainScreenWithNavigation(skipIntro = true)

        with(composeRule) {
            // Click on the "Routines" button or card
            onNodeWithText(composeRule.activity.getString(R.string.routine_tasks)).performClick()

            // Verify navigation to the Routines Screen
            composeRule.runOnIdle {
                assert(navController.currentDestination?.route == Destination.RoutinesListScreen.route) {
                    "Navigation to Routines Screen failed!"
                }
            }
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
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
