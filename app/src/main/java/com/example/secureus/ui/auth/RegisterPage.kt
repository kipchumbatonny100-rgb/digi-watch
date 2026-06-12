package com.example.secureus.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import android.widget.Toast
import com.example.secureus.R
import com.example.secureus.data.SecureUsApiService
import com.example.secureus.ui.theme.SecureUsTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun RegisterPage(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(value = false) }
    
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = remember { 
        if (FirebaseApp.getApps(context).isNotEmpty()) FirebaseAuth.getInstance() else null 
    }
    val apiService = remember { SecureUsApiService.create() }
    
    val fillAllFieldsMsg = stringResource(R.string.msg_fill_all_fields)
    val passwordsDontMatchMsg = stringResource(R.string.msg_passwords_dont_match)
    val registrationSuccessfulMsg = stringResource(R.string.msg_registration_successful)

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
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DigiLogo()

                    Spacer(modifier = Modifier.height(8.dp))

                    DigiTextField(
                        value = fullName, 
                        onValueChange = { fullName = it }, 
                        label = stringResource(R.string.label_full_name),
                        leadingIcon = Icons.Default.Person
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DigiTextField(
                        value = email, 
                        onValueChange = { email = it }, 
                        label = stringResource(R.string.label_email),
                        leadingIcon = Icons.Default.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DigiTextField(
                        value = password, 
                        onValueChange = { password = it }, 
                        label = stringResource(R.string.label_password), 
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DigiTextField(
                        value = confirmPassword, 
                        onValueChange = { confirmPassword = it }, 
                        label = stringResource(R.string.label_confirm_password), 
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                    } else {
                        GlossyButton(
                            text = stringResource(R.string.button_register),
                            onClick = {
                                if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
                                    Toast.makeText(context, fillAllFieldsMsg, Toast.LENGTH_SHORT).show()
                                    return@GlossyButton
                                }
                                if (password != confirmPassword) {
                                    Toast.makeText(context, passwordsDontMatchMsg, Toast.LENGTH_SHORT).show()
                                    return@GlossyButton
                                }
                                
                                isLoading = true
                                
                                // Attempt Firebase
                                if (auth != null) {
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                isLoading = false
                                                Toast.makeText(context, registrationSuccessfulMsg, Toast.LENGTH_SHORT).show()
                                                onRegisterSuccess()
                                            } else {
                                                // Fallback to Local API
                                                scope.launch {
                                                    registerViaApi(apiService, fullName, email, password, context, onRegisterSuccess) { isLoading = false }
                                                }
                                            }
                                        }
                                } else {
                                    // Preview mode or Firebase not initialized
                                    scope.launch {
                                        registerViaApi(apiService, fullName, email, password, context, onRegisterSuccess) { isLoading = false }
                                    }
                                }
                            },
                            colors = GlossyBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = onLoginClick) {
                        Text(
                            text = buildAnnotatedString {
                                append(stringResource(R.string.text_already_have_account))
                                withStyle(style = SpanStyle(color = Color(0xFF1976D2))) {
                                    append(stringResource(R.string.text_login))
                                }
                            },
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

private suspend fun registerViaApi(
    apiService: SecureUsApiService,
    fullName: String,
    email: String,
    password: String,
    context: android.content.Context,
    onRegisterSuccess: () -> Unit,
    onFinished: () -> Unit
) {
    try {
        val response = apiService.register(mapOf(
            "name" to fullName,
            "email" to email,
            "password" to password
        ))
        onFinished()
        if (response.status == "success") {
            Toast.makeText(context, context.getString(R.string.msg_registered_local_api), Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
        } else {
            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
        }
    } catch (e: retrofit2.HttpException) {
        onFinished()
        val errorBody = e.response()?.errorBody()?.string()
        Toast.makeText(context, context.getString(R.string.msg_registration_failed, errorBody), Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        onFinished()
        Toast.makeText(context, context.getString(R.string.msg_api_error, e.localizedMessage), Toast.LENGTH_LONG).show()
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPagePreview() {
    SecureUsTheme {
        RegisterPage(
            onRegisterSuccess = {},
            onLoginClick = {}
        )
    }
}
