package com.example.secureus.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.secureus.ui.theme.SafeZoneTheme
import com.example.secureus.data.SafeZoneApiService
import com.example.secureus.data.UserReportRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun ReportScreen(onBackClick: () -> Unit, onMapClick: () -> Unit, selectedLocationName: String? = null) {
    var isSafeSelected by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var location by rememberSaveable { mutableStateOf(selectedLocationName ?: "") }
    var incidentType by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(value = false) }

    val incidentTypes = listOf("Theft/Robbery", "Assault", "Bulgary", "Phone Snatching", "Militia Activity", "Rape", "Other")

    // Update location if selected from map
    LaunchedEffect(selectedLocationName) {
        selectedLocationName?.let {
            location = it
        }
    }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { FirebaseFirestore.getInstance() }
    val auth = remember { FirebaseAuth.getInstance() }
    val apiService = remember { SafeZoneApiService.create() }

    AppBackground {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = GlassWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DigiLogo()
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Report Security Status",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SmallGlossyButton(
                            text = "Safe",
                            icon = Icons.Default.Check,
                            colors = GlossyGreen,
                            isSelected = isSafeSelected == true,
                            onClick = { 
                                isSafeSelected = true 
                                incidentType = "" // Reset type if safe
                            },
                            modifier = Modifier.weight(1f)
                        )
                        SmallGlossyButton(
                            text = "Unsafe",
                            icon = Icons.Default.PriorityHigh,
                            colors = GlossyRed,
                            isSelected = isSafeSelected == false,
                            onClick = { isSafeSelected = false },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (isSafeSelected == false) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Type of Insecurity",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            incidentTypes.forEach { type ->
                                FilterChip(
                                    selected = incidentType == type,
                                    onClick = { incidentType = type },
                                    label = { Text(type) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFFD32F2F),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    DigiTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Enter Location",
                        leadingIcon = Icons.Default.LocationOn
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onMapClick) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pick from Map", color = Color(0xFF1976D2))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                    } else {
                        GlossyButton(
                            text = "Submit Report",
                            onClick = {
                                if (isSafeSelected == null || location.isBlank()) {
                                    Toast.makeText(context, "Please select status and enter location", Toast.LENGTH_SHORT).show()
                                    return@GlossyButton
                                }
                                
                                if (isSafeSelected == false && incidentType.isBlank()) {
                                    Toast.makeText(context, "Please specify the type of insecurity", Toast.LENGTH_SHORT).show()
                                    return@GlossyButton
                                }
                                
                                val userId = auth.currentUser?.uid ?: "anonymous"
                                val reportData = hashMapOf(
                                    "userId" to userId,
                                    "address" to location,
                                    "isSafe" to isSafeSelected!!,
                                    "incidentType" to incidentType,
                                    "timestamp" to com.google.firebase.Timestamp.now()
                                )

                                isLoading = true
                                
                                // Attempt Firebase Firestore
                                db.collection("reports")
                                    .add(reportData)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Report submitted to Cloud", Toast.LENGTH_SHORT).show()
                                        onBackClick()
                                    }
                                    .addOnFailureListener {
                                        // Fallback to Local API
                                        scope.launch {
                                            try {
                                                val response = apiService.submitReport(
                                                    UserReportRequest(
                                                        userId = 1, // Default ID for local test
                                                        address = location,
                                                        isSafe = isSafeSelected!!,
                                                        incidentType = incidentType
                                                    )
                                                )
                                                isLoading = false
                                                Toast.makeText(context, "Local API: ${response.message}", Toast.LENGTH_LONG).show()
                                                if (response.status == "success") onBackClick()
                                            } catch (e: Exception) {
                                                isLoading = false
                                                Toast.makeText(context, "API Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                            },
                            colors = GlossyBlue
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextButton(onClick = onBackClick, enabled = !isLoading) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun SmallGlossyButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: List<Color>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .height(50.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    if (isSelected) colors else listOf(Color.LightGray, Color.Gray)
                ),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    SafeZoneTheme {
        ReportScreen(onBackClick = {}, onMapClick = {})
    }
}
