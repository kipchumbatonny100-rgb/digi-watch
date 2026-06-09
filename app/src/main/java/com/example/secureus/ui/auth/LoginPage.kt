package com.example.secureus.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginPage(
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DigiLogo()

            Spacer(modifier = Modifier.height(32.dp))

            DigiTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username"
            )

            Spacer(modifier = Modifier.height(16.dp))

            DigiTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            DigiButton(
                text = "Login",
                onClick = onLogin
            )

            Spacer(modifier = Modifier.height(16.dp))

            DigiSecondaryButton(
                text = "Register",
                onClick = onRegister
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = { /* TODO */ }) {
                Text("Forgot password?", color = Color.Gray)
            }
        }
    }
}
