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
class UITestAboutScreen {

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
        launchAboutScreen()

        with(composeRule) {
            // Assert that the title of the screen is displayed
            onNodeWithText(composeRule.activity.getString(R.string.about_app))
                .assertIsDisplayed()
        }
    }

    @Test
    fun test2_author_text_is_displayed() {
        launchAboutScreen()

        with(composeRule) {
            // Assert that the author text is displayed
            onNodeWithText(composeRule.activity.getString(R.string.author) + ": xkolari1 ZS 2024/25")
                .assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun launchAboutScreen() {
        composeRule.activity.setContent {
            MaterialTheme {
                navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = Destination.AboutScreen.route
                )
            }
        }
    }
}