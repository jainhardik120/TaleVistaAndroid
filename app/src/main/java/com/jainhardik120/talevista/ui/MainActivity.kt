package com.jainhardik120.talevista.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.talevista.ui.presentation.RootNavigationGraph
import com.jainhardik120.talevista.ui.theme.TaleVistaTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    TaleVistaTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            RootNavigationGraph(navController = rememberNavController())
                        }
                    }
                }
            }
        )
    }
}