package cz.mendelu.project.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GasMeter
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Propane
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.GridBasedAlgorithm
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import cz.mendelu.pef.compose.todo.ui.elements.PlaceholderScreenContent
import cz.mendelu.project.R
import cz.mendelu.project.communication.FuelType
import cz.mendelu.project.communication.GasStationItem
import cz.mendelu.project.map.CustomMapRenderer
import cz.mendelu.project.navigation.INavigationRouter
import cz.mendelu.project.ui.elements.BaseScreen

// Default Brno coordinates
private val DEFAULT_LOCATION = LatLng(49.1951, 16.6068)
private const val DEFAULT_ZOOM = 10f

@Composable
fun MapScreen(navigationRouter: INavigationRouter) {
    val viewModel = hiltViewModel<MapScreenViewModel>()
    val context = LocalContext.current

    // Location client
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // State for user location
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // Permission state
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            getUserLocation(fusedLocationClient) { location ->
                userLocation = location
                Log.d("petstoredebug", "Location permission allowed")
                viewModel.loadGasStationData(userLocation!!.latitude, userLocation!!.longitude)
            }
        } else {
            Log.d("petstoredebug", "Location permission denied")
            viewModel.loadGasStationData(49.1951, 16.6068)
        }
    }

    // Request location on first launch
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getUserLocation(fusedLocationClient) { location ->
                userLocation = location
                viewModel.loadGasStationData(userLocation!!.latitude, userLocation!!.longitude)
            }
        }
    }

    var placeholder: PlaceholderScreenContent? = null
    val gasStations = remember { mutableStateOf<List<GasStationItem>>(emptyList()) }
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    state.value.let {
        when (it) {
            MapScreenUIState.Loading -> {
                //viewModel.loadGasStationData(49.1951, 16.6068)
            }
            is MapScreenUIState.DataLoaded -> {
                gasStations.value = it.gasStations
            }
            is MapScreenUIState.Error -> {
                placeholder = PlaceholderScreenContent(
                    image = null,
                    title = stringResource(it.error.communicationError),
                    text = null,
                    buttonText = stringResource(R.string.try_again_later),
                    onButtonClick = { navigationRouter.returnBack() }
                )
            }
        }
    }

    BaseScreen(
        topBarText = stringResource(R.string.gas_stations_nearby),
        showLoading = state.value is MapScreenUIState.Loading,
        onBackClick = { navigationRouter.returnBack() }
    ) {
        MapScreenContent(
            paddingValues = it,
            navigation = navigationRouter,
            gasStationItems = gasStations.value,
            initialLocation = userLocation ?: DEFAULT_LOCATION
        )
    }
}

@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreenContent(
    paddingValues: PaddingValues,
    navigation: INavigationRouter,
    gasStationItems: List<GasStationItem>,
    initialLocation: LatLng
) {
    val context = LocalContext.current
    val sheetState = rememberBottomSheetScaffoldState()
    var selectedSchool = remember { mutableStateOf<GasStationItem?>(null) }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                mapToolbarEnabled = false
            )
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, DEFAULT_ZOOM)
    }

    var googleMap by remember {
        mutableStateOf<GoogleMap?>(null)
    }

    var clusterRender by remember {
        mutableStateOf<CustomMapRenderer?>(null)
    }

    var clusterManager by remember {
        mutableStateOf<ClusterManager<GasStationItem>?>(null)
    }

    if (gasStationItems.isNotEmpty()) {
        clusterManager?.addItems(gasStationItems)
        clusterManager?.cluster()

    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {

        GoogleMap(
            modifier = Modifier.fillMaxHeight(),
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState
        ) {
            if (gasStationItems.isNotEmpty()) {
                MapEffect { map ->

                    if (googleMap == null) {
                        googleMap = map
                    }

                    if (clusterManager == null) {
                        clusterManager = ClusterManager(context, googleMap!!)
                        clusterRender = CustomMapRenderer(context, googleMap!!, clusterManager!!)

                        clusterManager?.apply {
                            algorithm = GridBasedAlgorithm()
                            renderer = clusterRender
                            addItems(gasStationItems)
                        }

                        clusterManager?.setOnClusterItemClickListener { school ->
                            selectedSchool.value = school

                            true
                        }
                    }

                    googleMap?.setOnCameraIdleListener {
                        clusterManager?.cluster()
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedSchool.value) {
        selectedSchool.value?.let {
            sheetState.bottomSheetState.expand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetContent = {
            selectedSchool.value?.let {
                BottomSheetContent(gasStationItem = it)
            }
        }
    ) {

    }
}

@Composable
fun BottomSheetContent(gasStationItem: GasStationItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = gasStationItem.name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        gasStationItem.fuelTypes.forEach { fuelType ->
            Row {
                FuelTypeIcon(fuelType = fuelType)
                Text(
                    text = stringResource(fuelType.displayName),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

        }
    }
}

@Composable
fun FuelTypeIcon(fuelType: FuelType) {
    when (fuelType) {
        FuelType.DIESEL -> Icon(
            Icons.Default.LocalGasStation,
            contentDescription = stringResource(R.string.diesel)
        )
        FuelType.LPG -> Icon(
            Icons.Default.Propane,
            contentDescription = stringResource(R.string.lpg)
        )
        FuelType.PETROL -> Icon(
            Icons.Default.GasMeter,
            contentDescription = stringResource(R.string.petrol)
        )
        FuelType.UNKNOWN -> Icon(
            Icons.Default.QuestionMark,
            contentDescription = stringResource(R.string.unknown_fuel)
        )
    }
}

private fun getUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (LatLng) -> Unit
) {
    try {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationReceived(LatLng(it.latitude, it.longitude))
                } ?: onLocationReceived(DEFAULT_LOCATION)
            }
            .addOnFailureListener {
                onLocationReceived(DEFAULT_LOCATION)
            }
    } catch (e: SecurityException) {
        onLocationReceived(DEFAULT_LOCATION)
    }
}