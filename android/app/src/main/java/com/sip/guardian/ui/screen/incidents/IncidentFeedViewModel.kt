package com.sip.guardian.ui.screen.incidents

import androidx.lifecycle.ViewModel
import com.sip.guardian.domain.model.Incident
import com.sip.guardian.domain.model.IncidentState
import com.sip.guardian.domain.model.ThreatType
import com.sip.guardian.domain.usecase.GetIncidentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FeedUiState(
    val loading: Boolean = true,
    val incidents: List<Incident> = emptyList(),
    val stateFilter: IncidentState? = null,
    val typeFilter: ThreatType? = null,
    val offline: Boolean = false,
)

@HiltViewModel
class IncidentFeedViewModel @Inject constructor(
    private val getIncidents: GetIncidentsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState

    init { refresh() }

    fun refresh() {
        val s = _uiState.value
        _uiState.value = s.copy(loading = true)
        Thread {
            try {
                val incidents = getIncidents.execute(
                    0, 50, s.stateFilter, s.typeFilter, null, null, null)
                _uiState.value = FeedUiState(
                    loading = false,
                    incidents = incidents,
                    stateFilter = s.stateFilter,
                    typeFilter = s.typeFilter,
                )
            } catch (e: Exception) {
                _uiState.value = s.copy(loading = false, offline = true)
            }
        }.start()
    }

    fun setStateFilter(state: IncidentState?) {
        _uiState.value = _uiState.value.copy(stateFilter = state)
        refresh()
    }

    fun setTypeFilter(type: ThreatType?) {
        _uiState.value = _uiState.value.copy(typeFilter = type)
        refresh()
    }
}
