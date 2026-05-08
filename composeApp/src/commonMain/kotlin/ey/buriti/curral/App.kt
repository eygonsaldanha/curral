package ey.buriti.curral

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ey.buriti.curral.navigation.NavigationState
import ey.buriti.curral.navigation.Screen
import ey.buriti.curral.ui.components.CurralBottomBar
import ey.buriti.curral.ui.components.QuickAddSheet
import ey.buriti.curral.ui.screens.AnimalDetailScreen
import ey.buriti.curral.ui.screens.AnimalGroupDetailScreen
import ey.buriti.curral.ui.screens.AnimaisScreen
import ey.buriti.curral.ui.screens.EstoqueScreen
import ey.buriti.curral.ui.screens.HomeScreen
import ey.buriti.curral.ui.screens.NovoAnimalScreen
import ey.buriti.curral.ui.screens.PerfilScreen
import ey.buriti.curral.ui.screens.ProducaoScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var navState by remember { mutableStateOf<NavigationState>(NavigationState.MainScreen(Screen.HOME)) }
        var showQuickAdd by remember { mutableStateOf(false) }

        val showBottomBar = navState is NavigationState.MainScreen

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    val currentScreen = (navState as NavigationState.MainScreen).screen
                    CurralBottomBar(
                        currentScreen = currentScreen,
                        onScreenSelected = { navState = NavigationState.MainScreen(it) },
                        onFabClick = { showQuickAdd = true },
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (val state = navState) {
                    is NavigationState.MainScreen -> when (state.screen) {
                        Screen.HOME -> HomeScreen(
                            onNavigateToProfile = { navState = NavigationState.Perfil },
                            onNavigateToAnimais = { navState = NavigationState.MainScreen(Screen.ANIMAIS) },
                        )
                        Screen.ANIMAIS -> AnimaisScreen(
                            onNavigateToAnimal = { navState = NavigationState.AnimalDetail(it) },
                            onNavigateToGroup = { navState = NavigationState.GroupDetail(it) },
                        )
                        Screen.PRODUCAO -> ProducaoScreen()
                        Screen.ESTOQUE -> EstoqueScreen()
                    }
                    is NavigationState.AnimalDetail -> AnimalDetailScreen(
                        animalId = state.animalId,
                        onBack = { navState = NavigationState.MainScreen(Screen.ANIMAIS) },
                        onNavigateToAnimal = { navState = NavigationState.AnimalDetail(it) },
                    )
                    is NavigationState.GroupDetail -> AnimalGroupDetailScreen(
                        groupId = state.groupId,
                        onBack = { navState = NavigationState.MainScreen(Screen.ANIMAIS) },
                        onNavigateToAnimal = { navState = NavigationState.AnimalDetail(it) },
                    )
                    NavigationState.NewAnimal -> NovoAnimalScreen(
                        onBack = { navState = NavigationState.MainScreen(Screen.ANIMAIS) },
                        onAnimalCreated = { id -> navState = NavigationState.AnimalDetail(id) },
                    )
                    NavigationState.Perfil -> PerfilScreen(
                        onBack = { navState = NavigationState.MainScreen(Screen.HOME) },
                    )
                }
            }
        }

        if (showQuickAdd) {
            QuickAddSheet(
                onDismiss = { showQuickAdd = false },
                onNavigateToNewAnimal = {
                    showQuickAdd = false
                    navState = NavigationState.NewAnimal
                },
            )
        }
    }
}