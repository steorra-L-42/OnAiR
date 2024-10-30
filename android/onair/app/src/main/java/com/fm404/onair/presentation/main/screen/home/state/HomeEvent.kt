package com.fm404.onair.presentation.main.screen.home.state

sealed class HomeEvent {
    data class OnNavigate(val route: String) : HomeEvent()
}