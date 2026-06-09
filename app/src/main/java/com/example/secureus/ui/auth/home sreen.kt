package com.example.secureus.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onReportClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF8F9FA),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = DigiGreen) },
                    label = { Text("Home", color = DigiGreen) },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Report") },
                    label = { Text("Report") },
                    selected = false,
                    onClick = onReportClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                    label = { Text("Logout") },
                    selected = false,
                    onClick = onLogoutClick
                )
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DigiLogo()

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Welcome, Kipchumba",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(32.dp))

            DigiButton(
                text = "Report Security Status",
                onClick = onReportClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // View Past Reports Button (Grey Gradient as per design)
            Button(
                onClick = { /* TODO */ },
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = "View Past Reports",
                    color = Color.Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}
