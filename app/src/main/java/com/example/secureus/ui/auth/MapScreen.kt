package com.example.secureus.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    onLocationSelected: (LatLng) -> Unit,
    initialLocation: LatLng = LatLng(-1.286389, 36.817223), // Nairobi default
) {
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            onMapClick = {
                selectedLocation = it
            }
        ) {
            val markerState = rememberMarkerState(position = selectedLocation)
            
            // Sync marker position with map clicks
            LaunchedEffect(selectedLocation) {
                markerState.position = selectedLocation
            }

            Marker(
                state = markerState,
                title = "Selected Location",
                snippet = "Report will be sent from here"
            )
        }

        FloatingActionButton(
            onClick = { onLocationSelected(selectedLocation) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Check, contentDescription = "Confirm Location")
        }
    }
}
