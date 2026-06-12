package com.example.secureus.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.secureus.data.SecureUsApiService
import com.example.secureus.data.UserStatusResponse
import com.example.secureus.ui.theme.SecureUsTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(onReportClick: () -> Unit, onPastReportsClick: () -> Unit, onLogoutClick: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val currentUser = auth.currentUser
    val userName = currentUser?.email?.split("@")?.get(0) ?: "User"
    val context = LocalContext.current
    val apiService = remember { SecureUsApiService.create() }
    val scope = rememberCoroutineScope()
    
    var userStatus by remember { mutableStateOf<UserStatusResponse?>(null) }

    // Fetch user status from backend on entry
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Fetching status for user ID 1 as a placeholder for local test
                userStatus = apiService.getUserStatus(1)
            } catch (e: Exception) {
                // Silent fail for status fetch
            }
        }
    }

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
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DigiLogo()
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Welcome, $userName",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    // Display Current Safety Status
                    userStatus?.let { status ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (status.isSafe) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (status.isSafe) "Status: SAFE" else "Status: UNSAFE",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (status.isSafe) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                                )
                                if (status.address.isNotBlank()) {
                                    Text(text = "at ${status.address}", fontSize = 12.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    GlossyButton(
                        text = "Report Security Status",
                        onClick = onReportClick,
                        colors = GlossyGreen,
                        icon = Icons.Default.Shield
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    GlossyButton(
                        text = "View Past Reports",
                        onClick = onPastReportsClick,
                        colors = GlossyBlue,
                        icon = Icons.Default.Assessment
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Bottom Navigation Mock as per design
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NavIconItem(Icons.Default.Home, "Home", isSelected = true)
                        NavIconItem(Icons.Default.LocationOn, "Report", isSelected = false, onClick = onReportClick)
                        NavIconItem(Icons.AutoMirrored.Filled.Help, "Settings", isSelected = false)
                        NavIconItem(
                            Icons.AutoMirrored.Filled.Logout, 
                            "Logout", 
                            isSelected = false
                        ) {
                            auth.signOut()
                            onLogoutClick()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavIconItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFF2E7D32) else Color(0xFF0D47A1),
                modifier = Modifier.size(32.dp)
            )
        }
        Text(label, fontSize = 12.sp, color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SecureUsTheme {
        HomeScreen(onReportClick = {}, onPastReportsClick = {}, onLogoutClick = {})
    }
}
