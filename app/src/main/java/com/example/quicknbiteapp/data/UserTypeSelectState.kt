package com.example.quicknbiteapp.data

sealed interface UserTypeSelectState {
    data object MainScreen : UserTypeSelectState
    data object TermsConditions : UserTypeSelectState
    data object PrivacyPolicy : UserTypeSelectState
    data object VendorAgreement : UserTypeSelectState
}