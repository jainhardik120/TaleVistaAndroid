package com.jainhardik120.talevista.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jainhardik120.talevista.ui.presentation.home.HomeScreen
import com.jainhardik120.talevista.ui.presentation.login.LoginScreen
import com.jainhardik120.talevista.ui.theme.TaleVistaTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.internal.InjectedFieldSignature
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TaleVistaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel : ActivityViewModel = hiltViewModel()
                    val authorized  = false
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = if(authorized){Screen.HomeScreen.route}else{Screen.LoginScreen.route}, route="Application"){
                        composable(route = Screen.LoginScreen.route){
                    LoginScreen()

                        }
                        composable(route = Screen.HomeScreen.route){
//                            HomeScreen()
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class ActivityViewModel @Inject constructor(private val sharedPreferences: SharedPreferences) : ViewModel(){
    fun isAuthorized() : Boolean{
        val token = sharedPreferences.getString("TOKEN", "null")
        return (token!=null && token!="null")
    }
}

sealed class Screen(val route: String){
    object LoginScreen : Screen("login_screen")
    object HomeScreen : Screen("home_screen")

    fun withArgs(vararg args: String):String{
        return buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}