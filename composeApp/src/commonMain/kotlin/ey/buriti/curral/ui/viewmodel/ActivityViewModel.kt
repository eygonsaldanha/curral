package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IActivityRepository
import ey.buriti.curral.model.ActivityLog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ActivityViewModel(repository: IActivityRepository) : ViewModel() {
    val activities: StateFlow<List<ActivityLog>> = repository.getActivities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
