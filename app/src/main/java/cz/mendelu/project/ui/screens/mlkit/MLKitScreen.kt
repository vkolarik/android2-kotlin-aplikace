package cz.mendelu.project.ui.screens.mlkit

import android.Manifest
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import cz.mendelu.project.R
import cz.mendelu.project.analyzers.TextRecognizer
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen
import cz.mendelu.project.ui.elements.CameraComposeView
import cz.mendelu.project.ui.theme.HalfMargin

@OptIn(ExperimentalPermissionsApi::class, ExperimentalLayoutApi::class)
@Composable
fun MLKitScreen(navigationRouter: INavigationRouter, id: Long) {
    val viewModel = hiltViewModel<MLKitScreenViewModel>()
    val state = viewModel.mlKitScreenUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(MLKitScreenData())
    }

    state.value.let {
        when (it) {
            is MLKitScreenUIState.Loading -> {
                viewModel.initialize(id)
            }

            is MLKitScreenUIState.DataChanged -> {
                data = it.data
            }

            is MLKitScreenUIState.ResultSaved -> {
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }
        }
    }

    var detectedTexts by remember { mutableStateOf<List<String>>(emptyList()) }
    var showCamera by remember { mutableStateOf(true) }
    var isRecognizing by remember { mutableStateOf(true) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    BaseScreen(
        topBarText = stringResource(R.string.text_recognition),
        onBackClick = { navigationRouter.returnBack() },
        showLoading = data.mlKitTask.id == null
    ) { paddingValues ->
        if (cameraPermissionState.status.isGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (showCamera) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        MLKitScreenContent(
                            onTextDetected = { if (isRecognizing) detectedTexts = it }
                        )
                    }

                    Button(
                        onClick = {
                            showCamera = false
                            isRecognizing = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(stringResource(R.string.that_s_correct))
                    }
                    Spacer(modifier = Modifier.height(HalfMargin()))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .padding(16.dp)
                    ) {

                        items(detectedTexts) { text ->
                            Text(
                                text = text,
                                modifier = Modifier.padding(vertical = HalfMargin())
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(HalfMargin()),
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(
                                    HalfMargin()
                                ),
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.mlkit_info)
                        )

                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                                .padding(bottom = HalfMargin()),
                            maxItemsInEachRow = Int.MAX_VALUE,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            detectedTexts.forEach { text ->
                                // Split the text by new line
                                val lines = text.split("\n")

                                // For each line, create a button
                                lines.forEach { line ->
                                    Button(
                                        onClick = { viewModel.setMlKitStringValue(line) },
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Text(line)
                                    }
                                }
                            }
                        }

                        TextField(
                            label = { Text(data.mlKitTask.description ?: "") },
                            modifier = Modifier
                                .fillMaxWidth(),
                            value = data.mlKitTask.returnValue ?: "",
                            onValueChange = {
                                data.mlKitTask.returnValue = it
                                viewModel.onMLKitScreenDataChanged(data)
                            },
                            singleLine = true,
                            isError = data.errors.returnValueError != null,
                            supportingText = {
                                if (data.errors.returnValueError != null) {
                                    Text(text = stringResource(id = data.errors.returnValueError!!))
                                }
                            }
                        )

                        Row(modifier = Modifier.align(Alignment.End)) {
                            Button(
                                onClick = {
                                    showCamera = true
                                    isRecognizing = true
                                }
                            ) {
                                Text(stringResource(R.string.try_again))
                            }

                            Button(
                                onClick = {
                                    viewModel.saveResult()
                                },
                                modifier = Modifier
                                    .padding(horizontal = HalfMargin())
                            ) {
                                Text("Done")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MLKitScreenContent(
    onTextDetected: (List<String>) -> Unit
) {
    val detectedTexts = remember { mutableStateListOf<String>() }

    val textRecognizer by remember {
        mutableStateOf<ImageAnalysis.Analyzer>(
            TextRecognizer { text ->
                detectedTexts.clear()
                detectedTexts.add(text)
                onTextDetected(detectedTexts.toList())
            }
        )
    }

    CameraComposeView(
        paddingValues = PaddingValues(0.dp),
        analyzer = textRecognizer
    )
}