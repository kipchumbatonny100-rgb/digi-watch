package com.example.secureus.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.secureus.data.SecureUsApiService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiService = remember { SecureUsApiService.create() }

    var showThemeDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showPhoneDialog by remember { mutableStateOf(false) }

    var themeColor by remember { mutableStateOf(Color(0xFF1976D2)) }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Placeholder User ID - in a real app, this would come from a Session Manager/Datastore
    val userId = "1" 

    AppBackground {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GlassWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Settings",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // User Profile Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(64.dp),
                            shape = RoundedCornerShape(32.dp),
                            color = themeColor.copy(alpha = 0.1f)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp).size(32.dp),
                                tint = themeColor
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = user?.email?.split("@")?.get(0)?.replaceFirstChar { it.uppercase() } ?: "User",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = user?.email ?: "No email available",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Settings Options
                    SettingsItem(icon = Icons.Default.Palette, title = "Change Theme Color", onClick = { showThemeDialog = true })
                    SettingsItem(icon = Icons.Default.LocationOn, title = "Update Location", onClick = { showLocationDialog = true })
                    SettingsItem(icon = Icons.Default.Phone, title = "Update Phone Number", onClick = { showPhoneDialog = true })
                    SettingsItem(icon = Icons.Default.Lock, title = "Change Password", onClick = { showPasswordDialog = true })

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "App Version 2.0.0",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }

        // Theme Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("Choose Theme Color", color = Color.Black) },
                text = {
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        ColorOption(Color(0xFF1976D2)) { themeColor = it; showThemeDialog = false }
                        ColorOption(Color(0xFF2E7D32)) { themeColor = it; showThemeDialog = false }
                        ColorOption(Color(0xFFD32F2F)) { themeColor = it; showThemeDialog = false }
                        ColorOption(Color(0xFF7B1FA2)) { themeColor = it; showThemeDialog = false }
                    }
                },
                confirmButton = { 
                    TextButton(onClick = { 
                        showThemeDialog = false 
                    }) { Text("Close") } 
                }
            )
        }

        // Location Dialog
        if (showLocationDialog) {
            var tempLocation by remember { mutableStateOf(location) }
            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("Update Location") },
                text = {
                    DigiTextField(
                        value = tempLocation,
                        onValueChange = { tempLocation = it },
                        label = "Address/Location",
                        leadingIcon = Icons.Default.LocationOn
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        location = tempLocation
                        scope.launch {
                            try {
                                apiService.updateSettings(mapOf("userId" to userId, "address" to location))
                                Toast.makeText(context, "Location updated", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                        showLocationDialog = false
                    }) { Text("Save") }
                },
                dismissButton = { TextButton(onClick = { showLocationDialog = false }) { Text("Cancel") } }
            )
        }

        // Phone Dialog
        if (showPhoneDialog) {
            var tempPhone by remember { mutableStateOf(phone) }
            AlertDialog(
                onDismissRequest = { showPhoneDialog = false },
                title = { Text("Update Phone Number") },
                text = {
                    DigiTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        label = "Phone Number",
                        leadingIcon = Icons.Default.Phone
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        phone = tempPhone
                        scope.launch {
                            try {
                                apiService.updateSettings(mapOf("userId" to userId, "phone" to phone))
                                Toast.makeText(context, "Phone number updated", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {}
                        }
                        showPhoneDialog = false
                    }) { Text("Save") }
                },
                dismissButton = { TextButton(onClick = { showPhoneDialog = false }) { Text("Cancel") } }
            )
        }

        // Password Dialog
        if (showPasswordDialog) {
            var oldPwd by remember { mutableStateOf("") }
            var newPwd by remember { mutableStateOf("") }
            var confirmPwd by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showPasswordDialog = false },
                title = { Text("Change Password") },
                text = {
                    Column {
                        DigiTextField(value = oldPwd, onValueChange = { oldPwd = it }, label = "Old Password", leadingIcon = Icons.Default.Lock, visualTransformation = PasswordVisualTransformation())
                        DigiTextField(value = newPwd, onValueChange = { newPwd = it }, label = "New Password", leadingIcon = Icons.Default.Lock, visualTransformation = PasswordVisualTransformation())
                        DigiTextField(value = confirmPwd, onValueChange = { confirmPwd = it }, label = "Confirm New Password", leadingIcon = Icons.Default.Lock, visualTransformation = PasswordVisualTransformation())
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newPwd != confirmPwd) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        scope.launch {
                            try {
                                val response = apiService.changePassword(mapOf(
                                    "userId" to userId,
                                    "oldPassword" to oldPwd,
                                    "newPassword" to newPwd
                                ))
                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                if (response.status == "success") showPasswordDialog = false
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) { Text("Change") }
                },
                dismissButton = { TextButton(onClick = { showPasswordDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
fun ColorOption(color: Color, onClick: (Color) -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick(color) }
    )
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1976D2))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontWeight = FontWeight.Medium, color = Color.Black)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = ">", color = Color.Gray)
        }
    }
}
