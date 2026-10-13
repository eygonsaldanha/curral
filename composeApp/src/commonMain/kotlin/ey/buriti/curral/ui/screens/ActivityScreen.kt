package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.model.ActivityLog
import ey.buriti.curral.model.ActivityType
import ey.buriti.curral.ui.theme.CurralColors
import ey.buriti.curral.ui.viewmodel.ActivityViewModel
import ey.buriti.curral.platform.getCurrentDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ActivityScreen(
    onOpenAnimal: (String) -> Unit,
    onOpenStock: (String) -> Unit,
    modifier: Modifier = Modifier,
    vm: ActivityViewModel = koinViewModel(),
) {
    val all by vm.activities.collectAsState()
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf<ActivityType?>(null) }
    val filtered = remember(all, query, filter) {
        all.filter { activity ->
            (filter == null || activity.type == filter) &&
                (query.isBlank() || listOf(activity.title, activity.description, activity.subjectName)
                    .any { it.contains(query, ignoreCase = true) })
        }
    }
    val grouped = remember(filtered) {
        filtered
            .sortedByDescending { it.occurredAt }
            .groupBy { it.occurredAt.take(10) }
    }

    Column(modifier.fillMaxSize().background(CurralColors.Background)) {
        Text("Atividade", color = CurralColors.TextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 16.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            placeholder = { Text("Buscar no histórico") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
        )
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActivityFilter("Tudo", filter == null) { filter = null }
            ActivityType.entries.forEach { type -> ActivityFilter(type.label, filter == type) { filter = type } }
        }
        if (grouped.isEmpty()) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.History, contentDescription = null, tint = CurralColors.NavInactive,
                    modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(12.dp))
                Text("Nenhuma atividade encontrada", color = CurralColors.TextSecondary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                grouped.forEach { (date, activities) ->
                    item(date) {
                        Text(formatActivityDate(date), color = CurralColors.TextPrimary, fontWeight = FontWeight.Bold,
                            fontSize = 18.sp, modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
                    }
                    items(activities, key = { it.id }) { activity ->
                        ActivityRow(activity) {
                            val id = activity.entityId ?: return@ActivityRow
                            when (activity.type) {
                                ActivityType.ANIMAL, ActivityType.GESTACAO -> onOpenAnimal(id)
                                ActivityType.ESTOQUE -> onOpenStock(id)
                                else -> Unit
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityFilter(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) CurralColors.FabGreen else CurralColors.CardSurface,
        contentColor = if (selected) Color.Black else CurralColors.TextPrimary,
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.clickable(onClick = onClick),
    ) { Text(label, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun ActivityRow(activity: ActivityLog, onClick: () -> Unit) {
    Surface(
        color = CurralColors.CardSurface,
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = CurralColors.SearchBackground, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(activityEmoji(activity.type), fontSize = 21.sp) }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(activity.title, color = CurralColors.TextPrimary, fontWeight = FontWeight.Bold)
                val subtitle = listOf(activity.subjectName, activity.description).filter { it.isNotBlank() }.joinToString(" • ")
                if (subtitle.isNotBlank()) Text(subtitle, color = CurralColors.TextSecondary, fontSize = 13.sp)
            }
            Text(activity.occurredAt.drop(11).take(5), color = CurralColors.TextSecondary, fontSize = 12.sp)
        }
    }
}

private fun activityEmoji(type: ActivityType) = when (type) {
    ActivityType.ANIMAL -> "🐄"
    ActivityType.PRODUCAO -> "📊"
    ActivityType.ESTOQUE -> "📦"
    ActivityType.GESTACAO -> "♡"
    ActivityType.GRUPO -> "◉"
}

private fun formatActivityDate(date: String): String {
    if (date == getCurrentDate().toIsoDateString()) return "Hoje"
    val parts = date.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else date
}
