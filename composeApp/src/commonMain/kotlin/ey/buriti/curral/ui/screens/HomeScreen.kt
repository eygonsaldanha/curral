package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.auth.AuthState
import ey.buriti.curral.auth.IAuthRepository
import ey.buriti.curral.model.AnimalStatus
import ey.buriti.curral.ui.theme.CurralColors
import ey.buriti.curral.ui.viewmodel.HomeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

enum class HomeTab(val label: String) { GERAL("Geral"), ANIMAIS("Animais"), ESTOQUE("Estoque") }

@Composable
fun HomeScreen(
    selectedTab: HomeTab,
    onTabChanged: (HomeTab) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnimal: (String) -> Unit,
    onNavigateToGroup: (String) -> Unit,
    onNavigateToStockItem: (String) -> Unit,
    onNavigateToProduction: () -> Unit,
    modifier: Modifier = Modifier,
    vm: HomeViewModel = koinViewModel(),
    authRepo: IAuthRepository = koinInject(),
) {
    val authState by authRepo.authState.collectAsState()
    val email = (authState as? AuthState.Authenticated)?.email.orEmpty()
    val userName = email.substringBefore("@").replace(".", " ").split(" ")
        .take(2).joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }.ifBlank { "Fazendeiro" }
    val initials = userName.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")

    Column(modifier.fillMaxSize().background(CurralColors.Background)) {
        HomeHeader(userName, initials, onNavigateToProfile)
        Box(Modifier.weight(1f)) {
            Box(Modifier.fillMaxSize().padding(top = 60.dp)) {
                when (selectedTab) {
                    HomeTab.GERAL -> GeneralDashboard(vm, onTabChanged, onNavigateToProduction)
                    HomeTab.ANIMAIS -> AnimaisScreen(onNavigateToAnimal, onNavigateToGroup)
                    HomeTab.ESTOQUE -> EstoqueScreen()
                }
            }
            HomeTabs(selectedTab, onTabChanged, Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun HomeHeader(name: String, initials: String, onProfile: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(shape = CircleShape, color = CurralColors.SearchBackground,
            modifier = Modifier.size(56.dp).clickable(onClick = onProfile)) {
            Box(contentAlignment = Alignment.Center) {
                Text(initials, color = CurralColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text("Olá,", color = CurralColors.TextSecondary, fontSize = 14.sp)
            Text(name, color = CurralColors.TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HomeTabs(selected: HomeTab, onSelected: (HomeTab) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth().widthIn(max = 680.dp).padding(horizontal = 20.dp, vertical = 4.dp),
        color = CurralColors.Surface.copy(alpha = 0.82f),
        shape = RoundedCornerShape(30.dp),
        shadowElevation = 12.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, CurralColors.NavInactive.copy(alpha = 0.25f)),
    ) {
        Row(Modifier.padding(5.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HomeTab.entries.forEach { tab ->
                Surface(
                    color = if (selected == tab) CurralColors.TextPrimary else Color.Transparent,
                    contentColor = if (selected == tab) CurralColors.Background else CurralColors.TextPrimary,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f).clickable { onSelected(tab) },
                ) { Text(tab.label, modifier = Modifier.padding(vertical = 10.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
private fun GeneralDashboard(vm: HomeViewModel, onTabChanged: (HomeTab) -> Unit, onProduction: () -> Unit) {
    val animals by vm.animals.collectAsState()
    val production by vm.recentProducao.collectAsState()
    val stock by vm.lowStockItems.collectAsState()
    val events by vm.upcomingEvents.collectAsState()
    val lowStock = stock.count { it.lowStockThreshold != null && it.quantity <= (it.lowStockThreshold ?: -1) }
    val healthy = animals.count { it.status == AnimalStatus.SAUDAVEL }

    Column(
        Modifier.fillMaxSize().wrapContentWidth(Alignment.CenterHorizontally).widthIn(max = 900.dp)
            .verticalScroll(rememberScrollState()).padding(20.dp).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Visão geral da fazenda", color = CurralColors.TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("Acompanhe o que precisa de atenção hoje.", color = CurralColors.TextSecondary)

        DashboardCard(
            icon = Icons.Default.Pets,
            title = "Animais",
            value = animals.size.toString(),
            subtitle = "$healthy saudáveis",
            onClick = { onTabChanged(HomeTab.ANIMAIS) },
        )
        DashboardCard(
            icon = Icons.AutoMirrored.Filled.ShowChart,
            title = "Produção",
            value = production.size.toString(),
            subtitle = "registros acompanhados",
            onClick = onProduction,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            MiniDashboardCard(Icons.Default.Inventory2, "Estoque", "${stock.size} itens", Modifier.weight(1f)) {
                onTabChanged(HomeTab.ESTOQUE)
            }
            MiniDashboardCard(Icons.Default.Warning, "Atenção", "$lowStock baixos", Modifier.weight(1f)) {
                onTabChanged(HomeTab.ESTOQUE)
            }
        }
        Surface(color = CurralColors.CardSurface, shape = RoundedCornerShape(26.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text("Próximas atividades", color = CurralColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                if (events.isEmpty()) Text("Nenhum evento agendado", color = CurralColors.TextSecondary)
                events.take(3).forEach { event ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(event.type.emoji, fontSize = 22.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(event.type.label, color = CurralColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                            Text(event.date, color = CurralColors.TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(icon: ImageVector, title: String, value: String, subtitle: String, onClick: () -> Unit) {
    Surface(color = CurralColors.CardSurface, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(22.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = CurralColors.SearchBackground, modifier = Modifier.size(54.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = CurralColors.FabGreen) }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = CurralColors.TextSecondary)
                Text(value, color = CurralColors.TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = CurralColors.TextSecondary, fontSize = 13.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = CurralColors.TextSecondary)
        }
    }
}

@Composable
private fun MiniDashboardCard(icon: ImageVector, title: String, value: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(color = CurralColors.CardSurface, shape = RoundedCornerShape(26.dp), modifier = modifier.height(150.dp).clickable(onClick = onClick)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, tint = CurralColors.FabGreen)
            Column {
                Text(title, color = CurralColors.TextSecondary)
                Text(value, color = CurralColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    }
}
