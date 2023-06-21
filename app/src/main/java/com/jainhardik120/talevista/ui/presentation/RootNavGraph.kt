package com.jainhardik120.talevista.ui.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.ui.presentation.home.HomeScreen
import com.jainhardik120.talevista.ui.presentation.login.LoginScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavigationGraph(navController: NavHostController) {
    val viewModel : RootViewModel = hiltViewModel()
    NavHost(navController = navController, route = "root_graph", startDestination = if(viewModel.isLoggedIn()){
        Screen.HomeScreen.route}else{
        Screen.LoginScreen.route}){
        composable(route = Screen.LoginScreen.route){
            LoginScreen(navigateUp = {
                navController.popBackStack()
                navController.navigate(it)
            })
        }
        composable(route = Screen.HomeScreen.route){
            HomeScreen(navigateUp = {
                navController.popBackStack()
                navController.navigate(it.route)
            })
        }
    }
}

@HiltViewModel
class RootViewModel @Inject constructor(private val authController: AuthController) : ViewModel(){
    fun isLoggedIn() : Boolean {
        return authController.isLoggedIn()
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