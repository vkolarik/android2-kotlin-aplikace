package cz.mendelu.project.ui.elements

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import cz.mendelu.project.R
import cz.mendelu.project.ui.theme.BasicMargin
import cz.mendelu.project.ui.theme.HalfMargin
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Function to copy an image to the app's storage
fun copyImageToAppStorage(context: Context, sourceUri: Uri): Uri? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US).format(Date())
    val fileName = "IMG_$timeStamp.jpg"
    val storageDir = context.getExternalFilesDir(null)
    val newFile = File(storageDir, fileName)

    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(sourceUri)
        val outputStream = FileOutputStream(newFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
                output.flush()
            }
        }

        Uri.fromFile(newFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun deleteImageFromAppStorage(uri: Uri): Boolean {
    return try {
        val file = File(uri.path ?: return false)
        if (file.exists() && file.isFile) {
            file.delete()
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// Composable function to load an image from a URI
@Composable
fun LoadImageFromUri(uriString: String) {
    val context = LocalContext.current
    val uri = Uri.parse(uriString)

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(uri)
            .crossfade(true)
            .transformations(RoundedCornersTransformation(HalfMargin().value))
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(HalfMargin())),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ProfileImage(uri: String?) {
    if (uri == null) {
        Box(
            modifier = Modifier
                .size(BasicMargin() * 3)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_splash),
                contentDescription = null,
                modifier = Modifier
                    .size(BasicMargin() * 3)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterStart
            )
        }

    } else {
        Box(
            modifier = Modifier
                .size(BasicMargin() * 3)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .transformations(RoundedCornersTransformation(HalfMargin().value))
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(HalfMargin())),
                contentScale = ContentScale.Crop
            )
            // Add other content like text
        }
    }
}

@Composable
fun BigImageFromUri(uri: String?) {
    if (uri != null) {
        Column(modifier = Modifier.padding(bottom = BasicMargin())) {
            LoadImageFromUri(uriString = uri)
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_image_is_set),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(BasicMargin())
            )
            Spacer(modifier = Modifier.height(BasicMargin()))
        }
    }
}
