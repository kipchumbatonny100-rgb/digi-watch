package com.example.secureus.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun RegisterPage(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DigiLogo()

            Spacer(modifier = Modifier.height(16.dp))

            DigiTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name")
            Spacer(modifier = Modifier.height(16.dp))
            DigiTextField(value = email, onValueChange = { email = it }, label = "Email")
            Spacer(modifier = Modifier.height(16.dp))
            DigiTextField(
                value = password, 
                onValueChange = { password = it }, 
                label = "Password", 
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            DigiTextField(
                value = confirmPassword, 
                onValueChange = { confirmPassword = it }, 
                label = "Confirm Password", 
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(32.dp))

            DigiButton(
                text = "Register",
                onClick = {
                    if (password == confirmPassword && fullName.isNotBlank() && email.isNotBlank()) {
                        onRegisterSuccess()
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onLoginClick) {
                Text(
                    text = buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(style = SpanStyle(color = DigiBlue)) {
                            append("Login")
                        }
                    },
                    color = Color.Gray
                )
            }
        }
    }
}
