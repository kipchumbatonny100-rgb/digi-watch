package com.example.secureus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.secureus.ui.auth.*
import com.example.secureus.ui.theme.SecureUsTheme
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth

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
    val auth = remember { FirebaseAuth.getInstance() }
    val startDestination = if (auth.currentUser != null) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        // Login Page
        composable("login") {
            LoginPage(
                onLogin = { role ->
                    if (role == "admin") {
                        navController.navigate("admin_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onRegister = { navController.navigate("register") },
                onForgotPassword = { navController.navigate("forgot_password") }
            )
        }

        // Forgot Password Page
        composable("forgot_password") {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Admin Dashboard
        composable("admin_dashboard") {
            AdminDashboardScreen(
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("admin_dashboard") { inclusive = true }
                    }
                }
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
                onPastReportsClick = { navController.navigate("past_reports") },
                onSettingsClick = { navController.navigate("settings") },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Settings Page
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Past Reports Page
        composable("past_reports") {
            PastReportsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // Report Page
        composable(
            route = "report?location={location}",
            arguments = listOf(navArgument("location") { defaultValue = "" })
        ) { backStackEntry ->
            val locationName = backStackEntry.arguments?.getString("location")
            ReportScreen(
                onBackClick = { navController.popBackStack() },
                onMapClick = { navController.navigate("map") },
                selectedLocationName = if (locationName.isNullOrBlank()) null else locationName
            )
        }

        // Map Page
        composable("map") {
            MapScreen(
                onLocationSelected = { latLng ->
                    val locString = "${latLng.latitude}, ${latLng.longitude}"
                    navController.navigate("report?location=$locString") {
                        popUpTo("report?location={location}") { inclusive = true }
                    }
                }
            )
        }
    }
}
