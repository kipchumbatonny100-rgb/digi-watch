package com.example.secureus.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import android.widget.Toast
import com.example.secureus.R
import com.example.secureus.data.SecureUsApiService
import com.example.secureus.ui.theme.SecureUsTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun LoginPage(onLogin: (String) -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(value = false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = remember { 
        if (FirebaseApp.getApps(context).isNotEmpty()) FirebaseAuth.getInstance() else null 
    }
    val apiService = remember { SecureUsApiService.create() }
    
    val enterEmailPasswordMsg = stringResource(R.string.msg_enter_email_password)
    val noInternetMsg = stringResource(R.string.msg_no_internet)
    val featureComingSoonMsg = stringResource(R.string.msg_feature_coming_soon)

    AppBackground {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = GlassWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DigiLogo()
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    DigiTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = stringResource(R.string.label_email),
                        leadingIcon = Icons.Default.Person
                    )
                    
                    DigiTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = stringResource(R.string.label_password),
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                    } else {
                        GlossyButton(
                            text = stringResource(R.string.text_login),
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, enterEmailPasswordMsg, Toast.LENGTH_SHORT).show()
                                    return@GlossyButton
                                }
                                
                                val cm = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
                                @Suppress("DEPRECATION")
                                val activeNetwork = cm.activeNetworkInfo
                                @Suppress("DEPRECATION")
                                if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting) {
                                    Toast.makeText(context, noInternetMsg, Toast.LENGTH_SHORT).show()
                                    return@GlossyButton
                                }

                                isLoading = true
                                
                                // Attempt Firebase first
                                if (auth != null) {
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                isLoading = false
                                                onLogin("user") // Default role for Firebase for now
                                            } else {
                                                // If Firebase fails, fallback to Local API
                                                scope.launch {
                                                    loginViaApi(apiService, email, password, context, onLogin) { isLoading = false }
                                                }
                                            }
                                        }
                                } else {
                                    // Preview mode or Firebase not initialized
                                    scope.launch {
                                        loginViaApi(apiService, email, password, context, onLogin) { isLoading = false }
                                    }
                                }
                            },
                            colors = GlossyGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = onRegister,
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = !isLoading).copy(width = 1.dp)
                    ) {
                        Text(stringResource(R.string.button_create_account), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TextButton(onClick = { 
                        Toast.makeText(context, featureComingSoonMsg, Toast.LENGTH_SHORT).show()
                    }) {
                        Text(stringResource(R.string.text_forgot_password), color = Color(0xFF1976D2))
                    }
                }
            }
        }
    }
}

private suspend fun loginViaApi(
    apiService: SecureUsApiService,
    email: String,
    password: String,
    context: android.content.Context,
    onLogin: (String) -> Unit,
    onFinished: () -> Unit
) {
    try {
        val response = apiService.login(mapOf("email" to email, "password" to password))
        onFinished()
        if (response.status == "success") {
            Toast.makeText(context, context.getString(R.string.msg_logged_in_api), Toast.LENGTH_SHORT).show()
            onLogin(response.user?.role ?: "user")
        } else {
            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
        }
    } catch (e: retrofit2.HttpException) {
        onFinished()
        val errorBody = e.response()?.errorBody()?.string()
        if (errorBody?.contains("Invalid") == true) {
            Toast.makeText(context, context.getString(R.string.msg_invalid_credentials), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, context.getString(R.string.msg_server_error, e.code()), Toast.LENGTH_LONG).show()
        }
    } catch (e: java.net.ConnectException) {
        onFinished()
        Toast.makeText(context, context.getString(R.string.msg_cannot_reach_server), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        onFinished()
        Toast.makeText(context, context.getString(R.string.msg_api_error, e.localizedMessage), Toast.LENGTH_LONG).show()
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    SecureUsTheme {
        LoginPage(onLogin = {}, onRegister = {})
    }
}
