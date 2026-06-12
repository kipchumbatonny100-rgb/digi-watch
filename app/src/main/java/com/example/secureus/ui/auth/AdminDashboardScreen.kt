package com.example.secureus.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.secureus.data.SafeZoneApiService
import com.example.secureus.data.UserReportRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(onLogoutClick: () -> Unit) {
    val apiService = remember { SafeZoneApiService.create() }
    var reports by remember { mutableStateOf<List<UserReportRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                reports = apiService.getAllReports()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load reports", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    AppBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text("Security Authority Dashboard", color = Color.Black, fontWeight = FontWeight.Bold) 
                    },
                    actions = {
                        IconButton(onClick = onLogoutClick) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Real-Time Emergency Alerts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val unsafeReports = reports.filter { !it.isSafe }
                        if (unsafeReports.isEmpty()) {
                            item {
                                Text("No active emergencies reported.", color = Color.DarkGray)
                            }
                        }
                        items(unsafeReports) { report ->
                            EmergencyReportItem(report)
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Recent Activity",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        
                        items(reports.filter { it.isSafe }) { report ->
                            ReportItem(report)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyReportItem(report: UserReportRequest) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Light Red
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "EMERGENCY: ${report.address}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFB71C1C)
                )
                Text(
                    text = "User reported UNSAFE status",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Button(
                    onClick = { 
                        Toast.makeText(context, "Authorities Dispatched to ${report.address}", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Dispatch Response", color = Color.White)
                }
            }
        }
    }
}
