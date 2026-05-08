package ey.buriti.curral.navigation

sealed class NavigationState {
    data class MainScreen(val screen: Screen) : NavigationState()
    data class AnimalDetail(val animalId: String) : NavigationState()
    data class GroupDetail(val groupId: String) : NavigationState()
    data object NewAnimal : NavigationState()
    data object Perfil : NavigationState()
}
