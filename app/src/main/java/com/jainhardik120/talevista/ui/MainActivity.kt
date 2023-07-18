package com.jainhardik120.talevista.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.jainhardik120.talevista.ui.presentation.RootNavigationGraph
import com.jainhardik120.talevista.ui.theme.TaleVistaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }
        checkForAppUpdates()
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

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(
                applicationContext,
                "Download successful. Restarting app in 5 seconds",
                Toast.LENGTH_LONG
            ).show()
            lifecycleScope.launch {
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }
    }

    private fun checkForAppUpdates() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                else -> false
            }
            if (isUpdateAllowed && isUpdateAvailable) {
                appUpdateManager.startUpdateFlowForResult(
                    info, updateType, this, 123
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        info, updateType, this, 123
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode != RESULT_OK) {
                println("Something went wrong updating...")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }
}