package com.example.secureus.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.secureus.data.SecureUsApiService
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(onBackClick: () -> Unit) {
    var identity by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("Email") }
    var step by remember { mutableIntStateOf(1) } // 1: Identity, 2: OTP & Reset
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiService = remember { SecureUsApiService.create() }

    AppBackground {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = GlassWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
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
                            text = "Reset Password",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (step == 1) {
                        Text("Choose reset method:", modifier = Modifier.fillMaxWidth(), color = Color.Black)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            RadioButton(selected = selectedMethod == "Email", onClick = { selectedMethod = "Email" })
                            Text("Email", color = Color.Black)
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(selected = selectedMethod == "SMS", onClick = { selectedMethod = "SMS" })
                            Text("SMS", color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        DigiTextField(
                            value = identity,
                            onValueChange = { identity = it },
                            label = if (selectedMethod == "Email") "Email Address" else "Phone Number",
                            leadingIcon = if (selectedMethod == "Email") Icons.Default.Email else Icons.Default.Phone
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isLoading) {
                            CircularProgressIndicator()
                        } else {
                            GlossyButton(
                                text = "Send OTP",
                                onClick = {
                                    if (identity.isBlank()) {
                                        Toast.makeText(context, "Please enter your identity", Toast.LENGTH_SHORT).show()
                                        return@GlossyButton
                                    }
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val response = apiService.forgotPassword(mapOf(
                                                "identity" to identity,
                                                "method" to selectedMethod
                                            ))
                                            isLoading = false
                                            if (response.status == "success") {
                                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                                step = 2
                                            } else {
                                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            isLoading = false
                                            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                colors = GlossyBlue
                            )
                        }
                    } else {
                        Text("Enter the OTP sent to $identity", color = Color.Black, fontSize = 14.sp)
                        
                        DigiTextField(
                            value = otp,
                            onValueChange = { otp = it },
                            label = "6-Digit OTP",
                            leadingIcon = Icons.Default.Lock
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DigiTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = "New Password",
                            leadingIcon = Icons.Default.Lock,
                            visualTransformation = PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DigiTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm New Password",
                            leadingIcon = Icons.Default.Lock,
                            visualTransformation = PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (isLoading) {
                            CircularProgressIndicator()
                        } else {
                            GlossyButton(
                                text = "Reset Password",
                                onClick = {
                                    if (otp.length != 6) {
                                        Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                                        return@GlossyButton
                                    }
                                    if (newPassword != confirmPassword) {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                        return@GlossyButton
                                    }
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val response = apiService.resetPassword(mapOf(
                                                "identity" to identity,
                                                "otp" to otp,
                                                "newPassword" to newPassword
                                            ))
                                            isLoading = false
                                            if (response.status == "success") {
                                                Toast.makeText(context, "Password reset successful!", Toast.LENGTH_LONG).show()
                                                onBackClick()
                                            } else {
                                                Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                            }
                                        } catch (e: Exception) {
                                            isLoading = false
                                            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                colors = GlossyGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
