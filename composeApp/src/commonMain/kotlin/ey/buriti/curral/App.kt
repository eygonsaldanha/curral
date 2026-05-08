package ey.buriti.curral

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ey.buriti.curral.navigation.NavigationState
import ey.buriti.curral.navigation.Screen
import ey.buriti.curral.platform.AppBackHandler
import ey.buriti.curral.platform.rememberExitApp
import ey.buriti.curral.ui.components.CurralBottomBar
import ey.buriti.curral.ui.components.QuickAddPage
import ey.buriti.curral.ui.components.QuickAddRequest
import ey.buriti.curral.ui.components.QuickAddSheet
import ey.buriti.curral.ui.screens.AnimalDetailScreen
import ey.buriti.curral.ui.screens.AnimalGroupDetailScreen
import ey.buriti.curral.ui.screens.AnimaisScreen
import ey.buriti.curral.ui.screens.EditGestationScreen
import ey.buriti.curral.ui.screens.EstoqueScreen
import ey.buriti.curral.ui.screens.GestationResultScreen
import ey.buriti.curral.ui.screens.HomeScreen
import ey.buriti.curral.ui.screens.ManageAnimalGroupsScreen
import ey.buriti.curral.ui.screens.NovoAnimalScreen
import ey.buriti.curral.ui.screens.PerfilScreen
import ey.buriti.curral.ui.screens.ProducaoScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var backStack by remember { mutableStateOf(listOf<NavigationState>(NavigationState.MainScreen(Screen.HOME))) }
        var quickAddRequest by remember { mutableStateOf<QuickAddRequest?>(null) }
        var showExitConfirmation by remember { mutableStateOf(false) }
        val exitApp = rememberExitApp()
        val navState = backStack.last()

        fun push(state: NavigationState) {
            backStack = backStack + state
        }

        fun goToMain(screen: Screen) {
            backStack = listOf(NavigationState.MainScreen(screen))
        }

        fun goBack() {
            when {
                quickAddRequest != null -> quickAddRequest = null
                backStack.size > 1 -> backStack = backStack.dropLast(1)
                else -> showExitConfirmation = true
            }
        }

        AppBackHandler(onBack = ::goBack)

        val showBottomBar = navState is NavigationState.MainScreen

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    val currentScreen = (navState as NavigationState.MainScreen).screen
                    CurralBottomBar(
                        currentScreen = currentScreen,
                        onScreenSelected = ::goToMain,
                        onFabClick = { quickAddRequest = QuickAddRequest() },
                    )
                }
            },
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (val state = navState) {
                    is NavigationState.MainScreen -> when (state.screen) {
                        Screen.HOME -> HomeScreen(
                            onNavigateToProfile = { push(NavigationState.Perfil) },
                            onNavigateToAnimais = { goToMain(Screen.ANIMAIS) },
                        )
                        Screen.ANIMAIS -> AnimaisScreen(
                            onNavigateToAnimal = { push(NavigationState.AnimalDetail(it)) },
                            onNavigateToGroup = { push(NavigationState.GroupDetail(it)) },
                        )
                        Screen.PRODUCAO -> ProducaoScreen()
                        Screen.ESTOQUE -> EstoqueScreen()
                    }
                    is NavigationState.AnimalDetail -> AnimalDetailScreen(
                        animalId = state.animalId,
                        onBack = ::goBack,
                        onNavigateToAnimal = { push(NavigationState.AnimalDetail(it)) },
                        onOpenAddEvent = {
                            quickAddRequest = QuickAddRequest(
                                initialPage = QuickAddPage.EVENTO,
                                preselectedAnimalId = it,
                            )
                        },
                        onOpenEditAnimal = {
                            quickAddRequest = QuickAddRequest(
                                initialPage = QuickAddPage.ANIMAL,
                                editingAnimalId = it,
                            )
                        },
                        onManageGroups = { push(NavigationState.ManageAnimalGroups(it)) },
                        onRegisterGestationResult = { push(NavigationState.GestationResult(it)) },
                        onEditGestation = { push(NavigationState.GestationEdit(it)) },
                    )
                    is NavigationState.GroupDetail -> AnimalGroupDetailScreen(
                        groupId = state.groupId,
                        onBack = ::goBack,
                        onNavigateToAnimal = { push(NavigationState.AnimalDetail(it)) },
                        onOpenBatchEvent = {
                            quickAddRequest = QuickAddRequest(
                                initialPage = QuickAddPage.EVENTO,
                                preselectedGroupId = it,
                            )
                        },
                    )
                    is NavigationState.ManageAnimalGroups -> ManageAnimalGroupsScreen(
                        animalId = state.animalId,
                        onBack = ::goBack,
                        onNavigateToAnimal = { push(NavigationState.AnimalDetail(it)) },
                    )
                    is NavigationState.GestationResult -> GestationResultScreen(
                        animalId = state.animalId,
                        onBack = ::goBack,
                    )
                    is NavigationState.GestationEdit -> EditGestationScreen(
                        animalId = state.animalId,
                        onBack = ::goBack,
                    )
                    NavigationState.NewAnimal -> NovoAnimalScreen(
                        onBack = ::goBack,
                        onAnimalCreated = { push(NavigationState.AnimalDetail(it)) },
                    )
                    NavigationState.Perfil -> PerfilScreen(onBack = ::goBack)
                }
            }
        }

        quickAddRequest?.let { request ->
            QuickAddSheet(
                onDismiss = { quickAddRequest = null },
                request = request,
                onAnimalSaved = { animalId ->
                    val editingAnimal = request.editingAnimalId != null
                    quickAddRequest = null
                    if (!editingAnimal) {
                        push(NavigationState.AnimalDetail(animalId))
                    }
                },
            )
        }

        if (showExitConfirmation) {
            AlertDialog(
                onDismissRequest = { showExitConfirmation = false },
                title = { Text("Sair do app") },
                text = { Text("Deseja mesmo sair do app?") },
                confirmButton = {
                    TextButton(onClick = {
                        showExitConfirmation = false
                        exitApp()
                    }) {
                        Text("Sair")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitConfirmation = false }) {
                        Text("Cancelar")
                    }
                },
            )
        }
    }
}
