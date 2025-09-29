package cz.mendelu.project.ui.screens.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cz.mendelu.project.R
import cz.mendelu.project.extensions.getAppVersionName
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.theme.BasicMargin
import cz.mendelu.project.ui.theme.HalfMargin

@Composable
fun AboutScreen(navigationRouter: INavigationRouter) {
    BaseScreen(
        topBarText = stringResource(R.string.about_app),
        onBackClick = { navigationRouter.returnBack() }
    ) {
        AboutScreenContent(paddingValues = it)
    }
}

@Composable
fun AboutScreenContent(
    paddingValues: PaddingValues
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(BasicMargin()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.author) +": xkolari1 ZS 2024/25",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = HalfMargin())
            )
            Text(
                text = stringResource(R.string.app_version) +": ${LocalContext.current.getAppVersionName()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
