package ey.buriti.curral

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ey.buriti.curral.auth.AuthState
import ey.buriti.curral.auth.IAuthRepository
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.data.IStockRepository
import ey.buriti.curral.domain.ScannedCode
import ey.buriti.curral.domain.parseScannedCode
import ey.buriti.curral.navigation.NavigationState
import ey.buriti.curral.navigation.Screen
import ey.buriti.curral.platform.AppBackHandler
import ey.buriti.curral.platform.CodeScanResult
import ey.buriti.curral.platform.CodeScanner
import ey.buriti.curral.platform.rememberExitApp
import ey.buriti.curral.ui.components.*
import ey.buriti.curral.ui.screens.*
import ey.buriti.curral.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val darkTheme = isSystemInDarkTheme()
    val curralColors = if (darkTheme) DarkCurralColors else LightCurralColors
    val materialColors = if (darkTheme) {
        darkColorScheme(primary = curralColors.FabGreen, background = curralColors.Background, surface = curralColors.Surface)
    } else {
        lightColorScheme(primary = curralColors.FabGreen, background = curralColors.Background, surface = curralColors.Surface)
    }

    CompositionLocalProvider(LocalCurralColors provides curralColors) {
        MaterialTheme(colorScheme = materialColors) {
            val authRepo: IAuthRepository = koinInject()
            val authState by authRepo.authState.collectAsState()
            val scope = rememberCoroutineScope()

            when (val state = authState) {
                AuthState.Loading -> { Box(Modifier.padding(24.dp)) { Text("Carregando…") }; return@MaterialTheme }
                AuthState.Unauthenticated -> { LoginScreen(); return@MaterialTheme }
                is AuthState.AwaitingEmailConfirmation -> {
                    EmailConfirmationScreen(email = state.email, onBackToLogin = { scope.launch { authRepo.signOut() } }); return@MaterialTheme
                }
                is AuthState.NeedsFarmSetup -> { FarmOnboardingScreen(); return@MaterialTheme }
                is AuthState.Authenticated -> Unit
            }

            AuthenticatedApp()
        }
    }
}

@Composable
private fun AuthenticatedApp() {
    val animalRepo: IAnimalRepository = koinInject()
    val stockRepo: IStockRepository = koinInject()
    val codeScanner: CodeScanner = koinInject()
    val scope = rememberCoroutineScope()
    val exitApp = rememberExitApp()

    var backStack by remember { mutableStateOf(listOf<NavigationState>(NavigationState.MainScreen(Screen.HOME))) }
    var homeTab by remember { mutableStateOf(HomeTab.GERAL) }
    var quickAddRequest by remember { mutableStateOf<QuickAddRequest?>(null) }
    var stockHighlightId by remember { mutableStateOf<String?>(null) }
    var showExitConfirmation by remember { mutableStateOf(false) }
    var showCodeDialog by remember { mutableStateOf(false) }
    var showGlobalSearch by remember { mutableStateOf(false) }
    var codeQuery by remember { mutableStateOf("") }
    var codeMessage by remember { mutableStateOf<String?>(null) }
    var ambiguousCodeResult by remember { mutableStateOf<AmbiguousCodeResult?>(null) }
    val navState = backStack.last()

    fun push(state: NavigationState) { backStack = backStack + state }
    fun goToMain(screen: Screen) { backStack = listOf(NavigationState.MainScreen(screen)) }
    fun goBack() {
        when {
            showGlobalSearch -> showGlobalSearch = false
            quickAddRequest != null -> Unit
            backStack.size > 1 -> backStack = backStack.dropLast(1)
            else -> showExitConfirmation = true
        }
    }
    fun openStock(id: String) {
        stockHighlightId = id
        homeTab = HomeTab.ESTOQUE
        goToMain(Screen.HOME)
    }
    fun contextualAdd() {
        quickAddRequest = when {
            navState == NavigationState.Production -> QuickAddRequest(initialPage = QuickAddPage.PRODUCAO)
            navState is NavigationState.MainScreen && navState.screen == Screen.HOME && homeTab == HomeTab.ANIMAIS ->
                QuickAddRequest(initialPage = QuickAddPage.ANIMAL)
            navState is NavigationState.MainScreen && navState.screen == Screen.HOME && homeTab == HomeTab.ESTOQUE ->
                QuickAddRequest(initialPage = QuickAddPage.ESTOQUE)
            else -> QuickAddRequest()
        }
    }
    fun resolveCode(raw: String) {
        val parsed = parseScannedCode(raw)
        val value = parsed.value
        if (value.isBlank()) return
        scope.launch {
            val animalId = (parsed as? ScannedCode.Animal)?.value
            val stockId = (parsed as? ScannedCode.Stock)?.value
            val animals = animalRepo.getAnimals().first()
            val stock = stockRepo.getItems().first()
            val animal = if (animalId != null) animals.firstOrNull { it.id == animalId }
                else animals.firstOrNull { it.id == value || it.tagNumber.equals(value, true) }
            val item = if (stockId != null) stock.firstOrNull { it.id == stockId }
                else stock.firstOrNull { it.id == value || it.code?.equals(value, true) == true }
            when {
                animal != null && item != null -> {
                    showCodeDialog = false
                    ambiguousCodeResult = AmbiguousCodeResult(
                        animalId = animal.id,
                        animalName = animal.name.ifBlank { animal.tagNumber },
                        stockId = item.id,
                        stockName = item.name,
                    )
                }
                animal != null -> { showCodeDialog = false; push(NavigationState.AnimalDetail(animal.id)) }
                item != null -> { showCodeDialog = false; openStock(item.id) }
                else -> codeMessage = "Código não encontrado. Cadastre-o em Animais ou Estoque."
            }
        }
    }

    AppBackHandler(onBack = ::goBack)
    val showDock = navState is NavigationState.MainScreen || navState == NavigationState.Production
    val currentPrimary = (navState as? NavigationState.MainScreen)?.screen ?: Screen.HOME

    Scaffold(containerColor = CurralColors.Background) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Box(Modifier.fillMaxSize().padding(bottom = if (showDock) 150.dp else 0.dp)) {
                when (val state = navState) {
                    is NavigationState.MainScreen -> when (state.screen) {
                        Screen.HOME -> HomeScreen(
                            selectedTab = homeTab,
                            onTabChanged = { homeTab = it },
                            onNavigateToProfile = { push(NavigationState.Perfil) },
                            onNavigateToAnimal = { push(NavigationState.AnimalDetail(it)) },
                            onNavigateToGroup = { push(NavigationState.GroupDetail(it)) },
                            onNavigateToStockItem = ::openStock,
                            onNavigateToProduction = { push(NavigationState.Production) },
                        )
                        Screen.ACTIVITY -> ActivityScreen(
                            onOpenAnimal = { push(NavigationState.AnimalDetail(it)) },
                            onOpenStock = ::openStock,
                        )
                    }
                    is NavigationState.AnimalDetail -> AnimalDetailScreen(
                        state.animalId, ::goBack,
                        onNavigateToAnimal = { push(NavigationState.AnimalDetail(it)) },
                        onOpenAddEvent = { quickAddRequest = QuickAddRequest(QuickAddPage.EVENTO, preselectedAnimalId = it) },
                        onOpenEditAnimal = { quickAddRequest = QuickAddRequest(QuickAddPage.ANIMAL, editingAnimalId = it) },
                        onManageGroups = { push(NavigationState.ManageAnimalGroups(it)) },
                        onRegisterGestationResult = { push(NavigationState.GestationResult(it)) },
                        onEditGestation = { push(NavigationState.GestationEdit(it)) },
                    )
                    is NavigationState.GroupDetail -> AnimalGroupDetailScreen(
                        state.groupId, ::goBack,
                        onNavigateToAnimal = { push(NavigationState.AnimalDetail(it)) },
                        onOpenBatchEvent = { quickAddRequest = QuickAddRequest(QuickAddPage.EVENTO, preselectedGroupId = it) },
                    )
                    is NavigationState.ManageAnimalGroups -> ManageAnimalGroupsScreen(
                        animalId = state.animalId,
                        onBack = ::goBack,
                        onNavigateToAnimal = { push(NavigationState.AnimalDetail(it)) },
                    )
                    is NavigationState.GestationResult -> GestationResultScreen(animalId = state.animalId, onBack = ::goBack)
                    is NavigationState.GestationEdit -> EditGestationScreen(animalId = state.animalId, onBack = ::goBack)
                    NavigationState.NewAnimal -> NovoAnimalScreen(onBack = ::goBack, onAnimalCreated = { push(NavigationState.AnimalDetail(it)) })
                    NavigationState.Perfil -> PerfilScreen(onBack = ::goBack)
                    NavigationState.Production -> ProducaoScreen(onBack = ::goBack)
                }
            }

            if (showDock) CurralBottomBar(
                currentScreen = currentPrimary,
                onScreenSelected = ::goToMain,
                onFabClick = ::contextualAdd,
                onSearchClick = { showGlobalSearch = true },
                onScanClick = {
                    scope.launch {
                        when (val result = codeScanner.scan()) {
                            is CodeScanResult.Value -> resolveCode(result.rawValue)
                            CodeScanResult.Cancelled -> Unit
                            is CodeScanResult.Unavailable -> {
                                codeQuery = ""
                                codeMessage = result.reason
                                showCodeDialog = true
                            }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )

            if (showGlobalSearch) {
                GlobalSearchOverlay(
                    animalRepo = animalRepo,
                    stockRepo = stockRepo,
                    onClose = { showGlobalSearch = false },
                    onAnimal = { showGlobalSearch = false; push(NavigationState.AnimalDetail(it)) },
                    onStock = { showGlobalSearch = false; openStock(it) },
                )
            }
        }
    }

    quickAddRequest?.let { request ->
        QuickAddSheet(
            onDismiss = { quickAddRequest = null }, request = request,
            onAnimalSaved = { id ->
                val editing = request.editingAnimalId != null
                quickAddRequest = null
                if (!editing) push(NavigationState.AnimalDetail(id))
            },
        )
    }

    if (showCodeDialog) {
        AlertDialog(
            onDismissRequest = { showCodeDialog = false },
            title = { Text("Buscar por código") },
            text = {
                androidx.compose.foundation.layout.Column {
                    Text("Aponte a câmera no celular ou informe o QR/código de barras manualmente.")
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
                    OutlinedTextField(codeQuery, { codeQuery = it }, singleLine = true,
                        placeholder = { Text("Código ou número do brinco") }, shape = RoundedCornerShape(16.dp))
                    codeMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                        if (it.startsWith("Código não encontrado")) {
                            androidx.compose.foundation.layout.Row(
                                Modifier.padding(top = 12.dp),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                            ) {
                                TextButton(onClick = {
                                    showCodeDialog = false
                                    quickAddRequest = QuickAddRequest(QuickAddPage.ANIMAL, initialCode = codeQuery.trim())
                                }) { Text("Cadastrar animal") }
                                TextButton(onClick = {
                                    showCodeDialog = false
                                    quickAddRequest = QuickAddRequest(QuickAddPage.ESTOQUE, initialCode = codeQuery.trim())
                                }) { Text("Cadastrar item") }
                            }
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { resolveCode(codeQuery) }, enabled = codeQuery.isNotBlank()) { Text("Buscar") } },
            dismissButton = { TextButton(onClick = { showCodeDialog = false }) { Text("Cancelar") } },
        )
    }

    ambiguousCodeResult?.let { result ->
        AlertDialog(
            onDismissRequest = { ambiguousCodeResult = null },
            title = { Text("Mais de um resultado") },
            text = {
                androidx.compose.foundation.layout.Column {
                    Text("O código informado corresponde a dois registros. Escolha qual deseja abrir.")
                    TextButton(onClick = {
                        ambiguousCodeResult = null
                        push(NavigationState.AnimalDetail(result.animalId))
                    }) { Text("Animal: ${result.animalName}") }
                    TextButton(onClick = {
                        ambiguousCodeResult = null
                        openStock(result.stockId)
                    }) { Text("Estoque: ${result.stockName}") }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { ambiguousCodeResult = null }) { Text("Cancelar") }
            },
        )
    }

    if (showExitConfirmation) {
        AlertDialog(
            onDismissRequest = { showExitConfirmation = false },
            title = { Text("Sair do app") }, text = { Text("Deseja mesmo sair do app?") },
            confirmButton = { TextButton(onClick = { showExitConfirmation = false; exitApp() }) { Text("Sair") } },
            dismissButton = { TextButton(onClick = { showExitConfirmation = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun GlobalSearchOverlay(
    animalRepo: IAnimalRepository,
    stockRepo: IStockRepository,
    onClose: () -> Unit,
    onAnimal: (String) -> Unit,
    onStock: (String) -> Unit,
) {
    val animals by remember(animalRepo) { animalRepo.getAnimals() }.collectAsState(emptyList())
    val stock by remember(stockRepo) { stockRepo.getItems() }.collectAsState(emptyList())
    var query by remember { mutableStateOf("") }
    val normalized = query.trim()
    val animalResults = remember(animals, normalized) {
        if (normalized.isBlank()) emptyList() else animals.filter {
            it.name.contains(normalized, true) || it.tagNumber.contains(normalized, true) ||
                it.breed.contains(normalized, true) || it.type.label.contains(normalized, true)
        }
    }
    val stockResults = remember(stock, normalized) {
        if (normalized.isBlank()) emptyList() else stock.filter {
            it.name.contains(normalized, true) || it.code?.contains(normalized, true) == true ||
                it.category.label.contains(normalized, true)
        }
    }

    Surface(Modifier.fillMaxSize(), color = CurralColors.Background) {
        Column(Modifier.fillMaxSize().widthIn(max = 760.dp).wrapContentWidth(Alignment.CenterHorizontally).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar por nome, brinco, código ou categoria") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                )
            }
            Spacer(Modifier.height(18.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (normalized.isBlank()) item { Text("Digite para buscar em animais e estoque.", color = CurralColors.TextSecondary) }
                if (normalized.isNotBlank() && animalResults.isEmpty() && stockResults.isEmpty()) {
                    item { Text("Nenhuma correspondência encontrada.", color = CurralColors.TextSecondary) }
                }
                if (animalResults.isNotEmpty()) item { Text("Animais", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) }
                items(animalResults, key = { "animal-${it.id}" }) { animal ->
                    SearchResult(Icons.Default.Pets, animal.name, "Brinco ${animal.tagNumber} · ${animal.breed}") { onAnimal(animal.id) }
                }
                if (stockResults.isNotEmpty()) item { Text("Estoque", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) }
                items(stockResults, key = { "stock-${it.id}" }) { item ->
                    SearchResult(Icons.Default.Inventory2, item.name, listOfNotNull(item.category.label, item.code).joinToString(" · ")) { onStock(item.id) }
                }
            }
        }
    }
}

@Composable
private fun SearchResult(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        color = CurralColors.CardSurface,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = CurralColors.FabGreen)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, color = CurralColors.TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = CurralColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private data class AmbiguousCodeResult(
    val animalId: String,
    val animalName: String,
    val stockId: String,
    val stockName: String,
)
