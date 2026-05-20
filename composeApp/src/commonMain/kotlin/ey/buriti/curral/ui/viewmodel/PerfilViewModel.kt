package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.auth.AuthState
import ey.buriti.curral.auth.IAuthRepository
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.data.IGroupRepository
import ey.buriti.curral.model.AnimalStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PerfilUiState(
    val email: String = "",
    val initials: String = "?",
    val totalAnimals: Int = 0,
    val healthyAnimals: Int = 0,
    val totalGroups: Int = 0,
)

class PerfilViewModel(
    private val authRepo: IAuthRepository,
    animalRepo: IAnimalRepository,
    groupRepo: IGroupRepository,
) : ViewModel() {

    val uiState: StateFlow<PerfilUiState> = combine(
        authRepo.authState,
        animalRepo.getAnimals(),
        groupRepo.getGroups(),
    ) { authState, animals, groups ->
        val email = (authState as? AuthState.Authenticated)?.email ?: ""
        val initials = email
            .split("@").first()
            .split(".")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
            .ifBlank { "?" }
        PerfilUiState(
            email = email,
            initials = initials,
            totalAnimals = animals.size,
            healthyAnimals = animals.count { it.status == AnimalStatus.SAUDAVEL },
            totalGroups = groups.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerfilUiState())

    fun signOut() = viewModelScope.launch {
        authRepo.signOut()
    }
}
