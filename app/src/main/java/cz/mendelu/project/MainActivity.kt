package cz.mendelu.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cz.mendelu.project.navigation.Destination
import cz.mendelu.project.navigation.NavGraph
import cz.mendelu.project.ui.theme.PlantyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /*@Inject
    lateinit var accomplishmentManager: AccomplishmentManager*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*CoroutineScope(Dispatchers.IO).launch {
            accomplishmentManager.generateAccomplishmentsForAllRoutines()
        }*/
        installSplashScreen()//.setKeepOnScreenCondition { return@setKeepOnScreenCondition vm.isLoading.value }
        setContent {
            PlantyTheme {
                NavGraph(startDestination = Destination.SummaryScreen.route)
            }
        }
    }
}
