package com.fm404.onair.features.statistics.presentation.main.state

sealed class StatisticsEvent {
    data object NavigateToBroadcastStatistics : StatisticsEvent()
    data object NavigateToStoryStatistics : StatisticsEvent()
}