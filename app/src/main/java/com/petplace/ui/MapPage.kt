package com.petplace.ui

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.petplace.MainViewModel
import com.petplace.model.HostingType
import com.petplace.ui.nav.Route

@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    navController: NavController

) {
    val camPosState = rememberCameraPositionState()
    val context = LocalContext.current
    val results = viewModel.searchResults
    val hasSearched = viewModel.hasSearched

    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(hasSearched, results) {
        if (hasSearched) {
            val first = results.firstOrNull()
            if (first?.lat != null && first.lng != null) {
                camPosState.position =
                    CameraPosition.fromLatLngZoom(
                        LatLng(first.lat, first.lng),
                        12f
                    )
            }
        }
    }


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = true),
        cameraPositionState = camPosState
    ) {
        if (hasSearched) {
            results.forEach { place ->
                val lat = place.lat
                val lng = place.lng

                if (lat != null && lng != null) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(lat, lng)
                        ),
                        title = place.name,
                        snippet = "${place.distance.toInt()} km de distância",
                        icon = BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        onClick = {
                            navController.navigate(
                                Route.HostingDescription(place.id)
                            )
                            true
                        }
                    )
                }
            }
        }
    }
}