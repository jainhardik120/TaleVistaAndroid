package com.jainhardik120.talevista.ui.presentation.login

data class LoginState(
    val loginEmail : String= "",
    val loginPassword : String= "",
    val registerFName : String= "",
    val registerLName : String= "",
    val registerEmail : String= "",
    val registerPassword : String= "",
    val registerRePassword : String= "",
    val currPage : LoginPage = LoginPage.ExistingAccount
)

enum class LoginPage{
    ExistingAccount,
    NewAccount
}