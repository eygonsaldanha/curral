package ey.buriti.curral.navigation

sealed class NavigationState {
    data class MainScreen(val screen: Screen) : NavigationState()
    data class AnimalDetail(val animalId: String) : NavigationState()
    data class GroupDetail(val groupId: String) : NavigationState()
    data class ManageAnimalGroups(val animalId: String) : NavigationState()
    data class GestationResult(val animalId: String) : NavigationState()
    data class GestationEdit(val animalId: String) : NavigationState()
    data object NewAnimal : NavigationState()
    data object Perfil : NavigationState()
}
