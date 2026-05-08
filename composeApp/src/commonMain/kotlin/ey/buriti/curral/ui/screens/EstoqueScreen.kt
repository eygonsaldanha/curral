package ey.buriti.curral.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.data.StockRepository
import ey.buriti.curral.model.StockCategory
import ey.buriti.curral.model.StockItem
import ey.buriti.curral.ui.theme.CurralColors

// ─── Badge helpers ──────────────────────────────────────────────────────────────

private val TODAY_YEAR  = 2026
private val TODAY_MONTH = 5
private val TODAY_DAY   = 7

private enum class StockBadge { NONE, BAIXO, VENCENDO, VENCIDO }

private fun stockBadge(item: StockItem): StockBadge {
    val threshold = item.lowStockThreshold
    if (threshold != null && item.quantity <= threshold) return StockBadge.BAIXO
    val expiry = item.expiryDate ?: return StockBadge.NONE
    return try {
        val parts = expiry.split("-")
        val y = parts[0].toInt(); val m = parts[1].toInt(); val d = parts[2].toInt()
        val todayDays = TODAY_YEAR * 365 + TODAY_MONTH * 30 + TODAY_DAY
        val expiryDays = y * 365 + m * 30 + d
        when {
            expiryDays < todayDays -> StockBadge.VENCIDO
            expiryDays - todayDays <= 60 -> StockBadge.VENCENDO
            else -> StockBadge.NONE
        }
    } catch (_: Exception) { StockBadge.NONE }
}

private fun formatExpiry(date: String): String = try {
    val p = date.split("-"); "${p[2]}/${p[1]}/${p[0]}"
} catch (_: Exception) { date }

// ─── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun EstoqueScreen(
    modifier: Modifier = Modifier,
    highlightItemId: String? = null,
    onHighlightConsumed: () -> Unit = {},
) {
    val items = StockRepository.items
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<StockCategory?>(null) }
    var localHighlightId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    // Clear filters and prepare to scroll when a highlight is requested
    LaunchedEffect(highlightItemId) {
        if (highlightItemId != null) {
            search = ""
            selectedCategory = null
            localHighlightId = highlightItemId
            onHighlightConsumed()
        }
    }

    val categories = StockCategory.entries
    val filtered = items.filter { item ->
        (selectedCategory == null || item.category == selectedCategory) &&
        (search.isBlank() || item.name.contains(search, ignoreCase = true))
    }

    // Scroll to the highlighted item once it appears in the list
    LaunchedEffect(localHighlightId, filtered.size) {
        val id = localHighlightId ?: return@LaunchedEffect
        val idx = filtered.indexOfFirst { it.id == id }
        if (idx >= 0) listState.animateScrollToItem(idx)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CurralColors.Background),
    ) {
        // ── Search bar ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CurralColors.Surface)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = CurralColors.NavInactive, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 14.sp, color = CurralColors.TextPrimary),
                cursorBrush = SolidColor(CurralColors.FabGreen),
                singleLine = true,
                decorationBox = { inner ->
                    if (search.isEmpty()) {
                        Text("Buscar no estoque...", fontSize = 14.sp, color = CurralColors.NavInactive)
                    }
                    inner()
                },
                interactionSource = remember { MutableInteractionSource() },
            )
        }

        // ── Category filter chips ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CategoryChip(label = "Todos", selected = selectedCategory == null, onClick = { selectedCategory = null })
            categories.forEach { cat ->
                CategoryChip(label = cat.label, selected = selectedCategory == cat, onClick = { selectedCategory = cat })
            }
        }

        // ── Items list ─────────────────────────────────────────────────────
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filtered, key = { it.id }) { item ->
                StockItemRow(
                    item = item,
                    isHighlighted = item.id == localHighlightId,
                    onIncrement = { StockRepository.increment(item.id) },
                    onDecrement = { StockRepository.decrement(item.id) },
                    onDelete = { StockRepository.remove(item.id) },
                )
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

// ─── Category Chip ─────────────────────────────────────────────────────────────

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) CurralColors.FabGreen else CurralColors.Surface
    val fg = if (selected) Color.White else CurralColors.TextPrimary
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bg,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = fg,
        )
    }
}

// ─── Stock Item Row ────────────────────────────────────────────────────────────

@Composable
private fun StockItemRow(
    item: StockItem,
    isHighlighted: Boolean = false,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit,
) {
    val badge = stockBadge(item)
    val isTool = item.category == StockCategory.FERRAMENTAS

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CurralColors.Surface,
        border = if (isHighlighted) BorderStroke(2.dp, CurralColors.AlertAccent) else null,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(categoryIconBg(item.category)),
                contentAlignment = Alignment.Center,
            ) {
                Text(item.category.emoji, fontSize = 18.sp)
            }

            Spacer(Modifier.width(12.dp))

            // Name + quantity info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
                    if (badge != StockBadge.NONE) {
                        BadgeChip(badge)
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        item.quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CurralColors.TextPrimary,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(item.unit, fontSize = 12.sp, color = CurralColors.TextSecondary, modifier = Modifier.padding(bottom = 2.dp))
                    val expiry = item.expiryDate
                    if (expiry != null) {
                        Text(
                            " • Venc: ${formatExpiry(expiry)}",
                            fontSize = 11.sp,
                            color = CurralColors.TextSecondary,
                            modifier = Modifier.padding(bottom = 2.dp),
                        )
                    }
                }
            }

            // Action buttons
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ActionIconButton(
                    onClick = onIncrement,
                    icon = Icons.Default.Add,
                    tint = CurralColors.FabGreen,
                    contentDescription = "Adicionar",
                )
                ActionIconButton(
                    onClick = onDecrement,
                    icon = Icons.Default.Remove,
                    tint = Color(0xFF1565C0),
                    contentDescription = "Remover",
                )
                if (!isTool) {
                    ActionIconButton(
                        onClick = onDelete,
                        icon = Icons.Default.Delete,
                        tint = Color(0xFFD32F2F),
                        contentDescription = "Excluir",
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionIconButton(onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, contentDescription: String) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun BadgeChip(badge: StockBadge) {
    val (bg, fg, label) = when (badge) {
        StockBadge.BAIXO    -> Triple(Color(0xFFFFF9C4), Color(0xFFF57F17), "Baixo")
        StockBadge.VENCENDO -> Triple(Color(0xFFFFE0B2), Color(0xFFE65100), "Vencendo")
        StockBadge.VENCIDO  -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), "Vencido")
        StockBadge.NONE     -> Triple(Color.Transparent, Color.Transparent, "")
    }
    if (badge == StockBadge.NONE) return
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = fg,
        )
    }
}

@Composable
private fun categoryIconBg(category: StockCategory): Color = when (category) {
    StockCategory.RACAO       -> CurralColors.CategoryRacaoBg
    StockCategory.FERRAMENTAS -> CurralColors.CategoryFerramentasBg
    StockCategory.REMEDIOS    -> CurralColors.CategoryRemediosBg
    StockCategory.OUTROS      -> CurralColors.CategoryOutrosBg
}

