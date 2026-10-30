package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.gsm.ui.DashboardScreen
import com.example.gsm.ui.GsmMainViewModel
import com.example.gsm.ui.LoginScreen
import com.example.ui.theme.GsmBackground
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GsmMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val isAuthenticated by viewModel.isAuthenticated.collectAsState()
                val loginError by viewModel.loginError.collectAsState()
                val currentLanguage by viewModel.currentLanguage.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GsmBackground)
                        .safeDrawingPadding()
                ) {
                    if (!isAuthenticated) {
                        LoginScreen(
                            currentLanguage = currentLanguage,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onLoginSuccess = { password ->
                                viewModel.verifyPassword(password)
                            },
                            errorMessage = loginError
                        )
                    } else {
                        DashboardScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
