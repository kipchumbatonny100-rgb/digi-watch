package com.example.secureus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavOptionsBuilder
import com.example.secureus.ui.auth.HomeScreen
import com.example.secureus.ui.auth.LoginPage
import com.example.secureus.ui.auth.RegisterPage
import com.example.secureus.ui.auth.ReportScreen
import com.example.secureus.ui.theme.SecureUsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SecureUsTheme {
                val navController = rememberNavController()
                SecureUsApp(navController = navController)
            }
        }
    }
}

@Composable
fun SecureUsApp(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        // Login Page
        composable("login") {
            LoginPage(
                onLogin = { navController.navigate("home") },
                onRegister = { navController.navigate("register") }
            )
        }

        // Register Page
        composable("register") {
            RegisterPage(
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable("home") {
            HomeScreen(
                onReportClick = { navController.navigate("report") },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Report Page
        composable("report") {
            ReportScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
