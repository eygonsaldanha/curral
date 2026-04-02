package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.ui.theme.CurralColors

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CurralColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        TopBar()
        SearchBar()
        Spacer(Modifier.height(16.dp))
        AlertsSection()
        Spacer(Modifier.height(16.dp))
        StatsSection()
        Spacer(Modifier.height(16.dp))
        CalendarSection()
        Spacer(Modifier.height(24.dp))
    }
}

// ─── Top Bar ────────────────────────────────────────────────────────────────────

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF37474F)),
            contentAlignment = Alignment.Center
        ) {
            Text("JS", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Bom dia!", fontSize = 13.sp, color = CurralColors.TextSecondary)
            Text("João Silva", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
        }
        IconButton(onClick = { /* TODO: abrir calendário */ }) {
            Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendário", tint = CurralColors.TextPrimary)
        }
        IconButton(onClick = { /* TODO: notificações */ }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notificações", tint = CurralColors.TextPrimary)
        }
    }
}

// ─── Search Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun SearchBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = CurralColors.SearchBackground
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = CurralColors.TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Buscar animais, eventos, estoque",
                color = CurralColors.TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.Tune, contentDescription = "Filtros", tint = CurralColors.TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

// ─── Alerts Section ─────────────────────────────────────────────────────────────

@Composable
private fun AlertsSection() {
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
                fontWeight = FontWeight.Medium
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
    var month by remember { mutableStateOf(4) }   // Abril
    var year by remember { mutableStateOf(2026) }

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
                    IconButton(onClick = { if (month == 1) { month = 12; year-- } else month-- }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mês anterior")
                    }
                    IconButton(onClick = { if (month == 12) { month = 1; year++ } else month++ }) {
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
            val today = 2    // hardcoded for demo; replace with platform date
            val todayMonth = 4
            val todayYear = 2026

            var dayCounter = 1
            for (week in 0..5) {
                if (dayCounter > totalDays) break
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dow in 0..6) {
                        val cellIndex = week * 7 + dow
                        if (cellIndex < firstDow || dayCounter > totalDays) {
                            Spacer(Modifier.weight(1f).height(36.dp))
                        } else {
                            val isToday = dayCounter == today && month == todayMonth && year == todayYear
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .then(
                                        if (isToday) Modifier
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(CurralColors.CalendarToday)
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayCounter.toString(),
                                    fontSize = 14.sp,
                                    color = if (isToday) Color.White else CurralColors.TextPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                            dayCounter++
                        }
                    }
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

