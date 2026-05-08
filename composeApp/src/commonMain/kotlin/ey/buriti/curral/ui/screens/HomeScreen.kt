package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.data.AnimalRepository
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.platform.PlatformDate
import ey.buriti.curral.platform.getCurrentDate
import ey.buriti.curral.ui.theme.CurralColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAnimais: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var showNotifications by remember { mutableStateOf(false) }
    var showAllAlerts by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CurralColors.Background)
            .verticalScroll(scrollState)
    ) {
        TopBar(
            onProfileClick = onNavigateToProfile,
            onCalendarClick = { scope.launch { scrollState.animateScrollTo(Int.MAX_VALUE) } },
            onNotificationsClick = { showNotifications = true },
        )
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { onNavigateToAnimais() },
        )
        Spacer(Modifier.height(16.dp))
        AlertsSection(onViewAll = { showAllAlerts = true })
        Spacer(Modifier.height(16.dp))
        StatsSection()
        Spacer(Modifier.height(16.dp))
        CalendarSection()
        Spacer(Modifier.height(24.dp))
    }

    if (showNotifications) {
        NotificationsSheet(onDismiss = { showNotifications = false })
    }
    if (showAllAlerts) {
        AllAlertsSheet(onDismiss = { showAllAlerts = false })
    }
}

// ─── Top Bar ────────────────────────────────────────────────────────────────────

@Composable
private fun TopBar(
    onProfileClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onNotificationsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar placeholder — clicável para abrir perfil
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF37474F))
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center
        ) {
            Text("JS", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onProfileClick)
        ) {
            Text("Bom dia!", fontSize = 13.sp, color = CurralColors.TextSecondary)
            Text("João Silva", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
        }
        IconButton(onClick = onCalendarClick) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendário", tint = CurralColors.TextPrimary)
        }
        IconButton(onClick = onNotificationsClick) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notificações", tint = CurralColors.TextPrimary)
        }
    }
}

// ─── Search Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = CurralColors.SearchBackground,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onSearch, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = CurralColors.TextSecondary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(4.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = CurralColors.TextPrimary),
                cursorBrush = SolidColor(CurralColors.TextPrimary),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text("Buscar animais, eventos, estoque", color = CurralColors.TextSecondary, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                },
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Limpar", tint = CurralColors.TextSecondary, modifier = Modifier.size(18.dp))
                }
            } else {
                Icon(Icons.Default.Tune, contentDescription = "Filtros", tint = CurralColors.TextSecondary, modifier = Modifier.size(20.dp).padding(end = 8.dp))
            }
        }
    }
}

// ─── Alerts Section ─────────────────────────────────────────────────────────────

@Composable
private fun AlertsSection(onViewAll: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.AlertBackground
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = CurralColors.AlertAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Alertas Importantes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CurralColors.TextPrimary
                )
            }
            Spacer(Modifier.height(12.dp))

            // Alert items
            AlertItem("Ração para galinhas próxima ao vencimento")
            Spacer(Modifier.height(8.dp))
            AlertItem("Antibiótico bovino vencido - descartar")

            Spacer(Modifier.height(12.dp))
            Text(
                "Ver todos os alertas →",
                color = CurralColors.AlertAccent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onViewAll),
            )
        }
    }
}

@Composable
private fun AlertItem(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CurralColors.AlertItemBackground
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(CurralColors.AlertDot)
            )
            Spacer(Modifier.width(10.dp))
            Text(text, fontSize = 14.sp, color = CurralColors.TextPrimary)
        }
    }
}

// ─── Notification & Alert Sheets ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("Notificações", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
            Spacer(Modifier.height(16.dp))
            NotificationItem(
                icon = Icons.Filled.Warning,
                iconColor = Color(0xFFEF4444),
                title = "Antibiótico Bovino vencido",
                subtitle = "Venceu em 09/08/2025 — descarte recomendado",
            )
            Spacer(Modifier.height(12.dp))
            NotificationItem(
                icon = Icons.Filled.Warning,
                iconColor = Color(0xFFF59E0B),
                title = "Ração para Galinhas vence em breve",
                subtitle = "Validade: 15/06/2026",
            )
            Spacer(Modifier.height(12.dp))
            NotificationItem(
                icon = Icons.Filled.WaterDrop,
                iconColor = Color(0xFF3B82F6),
                title = "Ração para Gado abaixo do mínimo",
                subtitle = "Estoque atual: 150 kg (mínimo: 200 kg)",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllAlertsSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Text("Todos os Alertas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
            Spacer(Modifier.height(16.dp))
            AlertItem("Antibiótico bovino vencido — descarte recomendado")
            Spacer(Modifier.height(8.dp))
            AlertItem("Ração para Galinhas próxima ao vencimento (15/06/2026)")
            Spacer(Modifier.height(8.dp))
            AlertItem("Ração para Gado abaixo do nível mínimo (150 kg / mín 200 kg)")
            Spacer(Modifier.height(8.dp))
            AlertItem("Faísca em tratamento — verificar evolução")
        }
    }
}

@Composable
private fun NotificationItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextPrimary)
            Text(subtitle, fontSize = 12.sp, color = CurralColors.TextSecondary)
        }
    }
}

// ─── Stats Section ──────────────────────────────────────────────────────────────

@Composable
private fun StatsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Filled.WaterDrop,
            iconBgColor = CurralColors.StatBlueBg,
            iconColor = CurralColors.StatBlue,
            label = "Leite Hoje",
            value = "245",
            unit = "litros",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = null,
            iconBgColor = CurralColors.StatOrangeBg,
            iconColor = CurralColors.StatOrange,
            label = "Ovos Hoje",
            value = "180",
            unit = "unid.",
            modifier = Modifier.weight(1f),
            iconText = "O" // placeholder for egg icon
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector?,
    iconBgColor: Color,
    iconColor: Color,
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    iconText: String? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (icon != null) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                    } else if (iconText != null) {
                        Text(iconText, color = iconColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(label, fontSize = 14.sp, color = CurralColors.TextSecondary)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
                Spacer(Modifier.width(4.dp))
                Text(unit, fontSize = 14.sp, color = CurralColors.TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

// ─── Calendar Section ───────────────────────────────────────────────────────────

@Composable
private fun CalendarSection() {
    val today = remember { getCurrentDate() }
    var month by remember { mutableStateOf(today.month) }
    var year by remember { mutableStateOf(today.year) }
    var selectedDay by remember { mutableStateOf(today.day) }
    val selectedDate = if (selectedDay in 1..daysInMonth(year, month)) toIsoDateString(year, month, selectedDay) else null
    val dayEvents = selectedDate?.let(AnimalRepository::getEventsForDay).orEmpty()
    val dayTasks = selectedDate?.let(AnimalRepository::getTasksForDay).orEmpty()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${monthName(month)} $year",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CurralColors.TextPrimary
                )
                Row {
                    IconButton(onClick = {
                        if (month == 1) {
                            month = 12
                            year--
                        } else {
                            month--
                        }
                        selectedDay = 1
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mês anterior")
                    }
                    IconButton(onClick = {
                        if (month == 12) {
                            month = 1
                            year++
                        } else {
                            month++
                        }
                        selectedDay = 1
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Próximo mês")
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            // Day-of-week headers (Portuguese)
            val dayHeaders = listOf("D", "S", "T", "Q", "Q", "S", "S")
            Row(modifier = Modifier.fillMaxWidth()) {
                dayHeaders.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = CurralColors.TextSecondary
                    )
                }
            }
            Spacer(Modifier.height(4.dp))

            // Day grid
            val firstDow = dayOfWeek(year, month, 1) // 0=Sun
            val totalDays = daysInMonth(year, month)

            var dayCounter = 1
            for (week in 0..5) {
                if (dayCounter > totalDays) break
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dow in 0..6) {
                        val cellIndex = week * 7 + dow
                        if (cellIndex < firstDow || dayCounter > totalDays) {
                            Spacer(Modifier.weight(1f).height(36.dp))
                        } else {
                            val currentDay = dayCounter
                            val isToday = currentDay == today.day && month == today.month && year == today.year
                            val isSelected = currentDay == selectedDay
                            val hasItems = AnimalRepository.getEventsForDay(toIsoDateString(year, month, currentDay)).isNotEmpty() ||
                                AnimalRepository.getTasksForDay(toIsoDateString(year, month, currentDay)).isNotEmpty()
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> CurralColors.FabGreen
                                            isToday -> CurralColors.CalendarToday
                                            else -> Color.Transparent
                                        },
                                    )
                                    .clickable { selectedDay = currentDay },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = currentDay.toString(),
                                        fontSize = 14.sp,
                                        color = if (isToday || isSelected) Color.White else CurralColors.TextPrimary,
                                        textAlign = TextAlign.Center,
                                    )
                                    if (hasItems) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 1.dp)
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isToday || isSelected) Color.White else CurralColors.FabGreen),
                                        )
                                    }
                                }
                            }
                            dayCounter++
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Agenda de ${selectedDate?.let(::formatDate).orEmpty()}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = CurralColors.TextPrimary,
            )
            Spacer(Modifier.height(8.dp))
            if (dayEvents.isEmpty() && dayTasks.isEmpty()) {
                Text(
                    "Nenhum evento ou tarefa para este dia.",
                    fontSize = 13.sp,
                    color = CurralColors.TextSecondary,
                )
            } else {
                dayTasks.forEach { task ->
                    AgendaRow(
                        emoji = "✅",
                        title = task.title,
                        subtitle = task.notes.ifBlank { "Tarefa do dia" },
                    )
                }
                dayEvents.forEach { event ->
                    AgendaRow(
                        emoji = event.type.emoji,
                        title = event.type.label,
                        subtitle = buildEventSubtitle(event),
                    )
                }
            }
        }
    }
}

// ─── Calendar helpers ───────────────────────────────────────────────────────────

private fun monthName(month: Int): String = when (month) {
    1 -> "Janeiro"; 2 -> "Fevereiro"; 3 -> "Março"; 4 -> "Abril"
    5 -> "Maio"; 6 -> "Junho"; 7 -> "Julho"; 8 -> "Agosto"
    9 -> "Setembro"; 10 -> "Outubro"; 11 -> "Novembro"; 12 -> "Dezembro"
    else -> ""
}

/** Returns 0=Sunday … 6=Saturday using Zeller-like formula. */
private fun dayOfWeek(year: Int, month: Int, day: Int): Int {
    var y = year; var m = month
    if (m < 3) { m += 12; y -= 1 }
    val k = y % 100; val j = y / 100
    val h = (day + (13 * (m + 1)) / 5 + k + k / 4 + j / 4 - 2 * j) % 7
    return ((h + 6) % 7 + 7) % 7 // normalize to 0..6
}

private fun daysInMonth(year: Int, month: Int): Int = when (month) {
    1, 3, 5, 7, 8, 10, 12 -> 31
    4, 6, 9, 11 -> 30
    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
    else -> 30
}

@Composable
private fun AgendaRow(emoji: String, title: String, subtitle: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CurralColors.Background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextPrimary)
                Text(subtitle, fontSize = 12.sp, color = CurralColors.TextSecondary)
            }
        }
    }
}

private fun buildEventSubtitle(event: AnimalEvent): String {
    val animalName = event.animalId.takeIf { it.isNotBlank() }?.let { AnimalRepository.getAnimal(it)?.name }
    val groupName = event.groupId?.let { AnimalRepository.getGroup(it)?.name }
    val target = animalName ?: groupName ?: "Evento geral"
    val details = listOf(event.time.takeIf { it.isNotBlank() }, event.notes.takeIf { it.isNotBlank() })
        .filterNotNull()
        .joinToString(" • ")
    return listOf(target, details).filter { it.isNotBlank() }.joinToString(" — ")
}

private fun toIsoDateString(year: Int, month: Int, day: Int): String =
    PlatformDate(year = year, month = month, day = day).toIsoDateString()
