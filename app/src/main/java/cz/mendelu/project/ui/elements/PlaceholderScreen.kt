package cz.mendelu.pef.compose.todo.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.mendelu.project.ui.theme.BasicMargin

data class PlaceholderScreenContent(val image: Int?,
                                    val title: String?,
                                    val text: String?,
                                    val buttonText: String? = null,
                                    val onButtonClick: (() -> Unit)? = null)

@Composable
fun PlaceHolderScreen(
    modifier: Modifier = Modifier,
    content: PlaceholderScreenContent){
    Box(modifier = modifier
        .fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(BasicMargin())) {

            if (content.image != null) {
                Image(
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.width(300.dp),
                    painter = painterResource(id = content.image),
                    contentDescription = null)
            }

            if (content.title != null){
                Spacer(modifier = Modifier.height(BasicMargin()))
                Text(text = content.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Black)
            }

            if (content.text != null){
                Spacer(modifier = Modifier.height(BasicMargin()))
                Text(text = content.text,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray)
            }
            if (content.buttonText != null && content.onButtonClick != null){
                Spacer(modifier = Modifier.height(BasicMargin()))
                OutlinedButton(
                    onClick = content.onButtonClick,
                ) {
                    Text(text = content.buttonText)
                }
            }
        }
    }
}