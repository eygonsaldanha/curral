package ey.buriti.curral

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
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
import ey.buriti.curral.ui.screens.EmailConfirmationScreen
import ey.buriti.curral.ui.screens.EstoqueScreen
import ey.buriti.curral.ui.screens.FarmOnboardingScreen
import ey.buriti.curral.ui.screens.GestationResultScreen
import ey.buriti.curral.ui.screens.HomeScreen
import ey.buriti.curral.ui.screens.ManageAnimalGroupsScreen
import ey.buriti.curral.ui.screens.NovoAnimalScreen
import ey.buriti.curral.ui.screens.PerfilScreen
import ey.buriti.curral.ui.screens.ProducaoScreen
import ey.buriti.curral.ui.theme.DarkCurralColors
import ey.buriti.curral.ui.theme.LightCurralColors
import ey.buriti.curral.ui.theme.LocalCurralColors

import ey.buriti.curral.auth.AuthState
import ey.buriti.curral.auth.IAuthRepository
import ey.buriti.curral.ui.screens.LoginScreen
import org.koin.compose.koinInject
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    val darkTheme = isSystemInDarkTheme()
    val curralColors = if (darkTheme) DarkCurralColors else LightCurralColors
    val materialColors = if (darkTheme) darkColorScheme() else lightColorScheme()
    CompositionLocalProvider(LocalCurralColors provides curralColors) {
        MaterialTheme(colorScheme = materialColors) {

        val authRepo: IAuthRepository = koinInject()
        val authState by authRepo.authState.collectAsState()
        val authScope = rememberCoroutineScope()

        when (val state = authState) {
            is AuthState.Loading -> {
                Box(modifier = Modifier.padding()) {
                    Text("Carregando…")
                }
                return@MaterialTheme
            }
            is AuthState.Unauthenticated -> {
                LoginScreen()
                return@MaterialTheme
            }
            is AuthState.AwaitingEmailConfirmation -> {
                EmailConfirmationScreen(
                    email = state.email,
                    onBackToLogin = { authScope.launch { authRepo.signOut() } },
                )
                return@MaterialTheme
            }
            is AuthState.NeedsFarmSetup -> {
                FarmOnboardingScreen()
                return@MaterialTheme
            }
            is AuthState.Authenticated -> Unit // continua
        }

        var backStack by remember { mutableStateOf(listOf<NavigationState>(NavigationState.MainScreen(Screen.HOME))) }
        var quickAddRequest by remember { mutableStateOf<QuickAddRequest?>(null) }
        var showExitConfirmation by remember { mutableStateOf(false) }
        var stockHighlightId by remember { mutableStateOf<String?>(null) }
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
                            onNavigateToAnimal = { id -> push(NavigationState.AnimalDetail(id)) },
                            onNavigateToStockItem = { id ->
                                stockHighlightId = id
                                goToMain(Screen.ESTOQUE)
                            },
                        )
                        Screen.ANIMAIS -> AnimaisScreen(
                            onNavigateToAnimal = { push(NavigationState.AnimalDetail(it)) },
                            onNavigateToGroup = { push(NavigationState.GroupDetail(it)) },
                        )
                        Screen.PRODUCAO -> ProducaoScreen()
                        Screen.ESTOQUE -> EstoqueScreen(
                            highlightItemId = stockHighlightId,
                            onHighlightConsumed = { stockHighlightId = null },
                        )
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
}
