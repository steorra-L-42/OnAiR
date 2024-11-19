package com.fm404.onair.features.statistics.presentation.main

import com.fm404.onair.core.common.base.BaseViewModel
import com.fm404.onair.features.statistics.presentation.main.state.StatisticsState
import com.fm404.onair.features.statistics.presentation.main.state.StatisticsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor() : BaseViewModel<StatisticsState, StatisticsEvent>() {
    override fun createInitialState(): StatisticsState = StatisticsState()

    override fun onEvent(event: StatisticsEvent) {
        when (event) {
            is StatisticsEvent.NavigateToBroadcastStatistics -> {
                setState { copy(isLoading = true) }
            }
            is StatisticsEvent.NavigateToStoryStatistics -> {
                setState { copy(isLoading = true) }
            }
        }
    }
}