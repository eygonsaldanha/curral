package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.data.AnimalRepository
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.model.AnimalStatus
import ey.buriti.curral.model.AnimalType
import ey.buriti.curral.ui.theme.CurralColors

@Composable
fun AnimaisScreen(
    onNavigateToAnimal: (String) -> Unit,
    onNavigateToGroup: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<AnimalType?>(null) }
    var showGroups by remember { mutableStateOf(false) }

    val allAnimals = AnimalRepository.animals
    val filteredAnimals = allAnimals.filter { animal ->
            val matchesQuery = searchQuery.isBlank() ||
                animal.name.contains(searchQuery, ignoreCase = true) ||
                animal.tagNumber.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedType == null || animal.type == selectedType
            matchesQuery && matchesType
        }

    val totalCount = AnimalRepository.animals.size
    val healthyCount = AnimalRepository.animals.count { it.status == AnimalStatus.SAUDAVEL }
    val pregnantCount = AnimalRepository.animals.count { it.status == AnimalStatus.PRENHA }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CurralColors.Background)
    ) {
        // ── Header ─────────────────────────────────────────────────────────────
        Column(modifier = Modifier.background(Color.White).padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                "Animais",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CurralColors.TextPrimary,
            )
            Spacer(Modifier.height(12.dp))
            AnimaisStatsRow(totalCount, healthyCount, pregnantCount)
            Spacer(Modifier.height(12.dp))
            AnimaisSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
        }

        // ── Quick type filter carousel ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TypeFilterChip(label = "Todos", isSelected = selectedType == null, onClick = { selectedType = null })
            AnimalType.entries.filter { it != AnimalType.OUTRO }.forEach { type ->
                TypeFilterChip(
                    label = "${type.emoji} ${type.label}",
                    isSelected = selectedType == type,
                    onClick = { selectedType = if (selectedType == type) null else type },
                )
            }
        }

        HorizontalDivider(color = CurralColors.SearchBackground)

        // ── View toggle (Lista / Grupos) ────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ViewToggleButton(label = "Lista", isSelected = !showGroups, onClick = { showGroups = false })
            ViewToggleButton(label = "Grupos", isSelected = showGroups, onClick = { showGroups = true })
        }

        HorizontalDivider(color = CurralColors.SearchBackground)

        // ── Content ────────────────────────────────────────────────────────────
        if (showGroups) {
            GroupsListView(
                groups = AnimalRepository.groups,
                onGroupClick = onNavigateToGroup,
            )
        } else {
            AnimalsListView(
                animals = filteredAnimals,
                onAnimalClick = onNavigateToAnimal,
            )
        }
    }
}

// ─── Stats Row ─────────────────────────────────────────────────────────────────

@Composable
private fun AnimaisStatsRow(total: Int, healthy: Int, pregnant: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatChip(label = "Total", value = "$total", bgColor = CurralColors.StatBlueBg, textColor = CurralColors.StatBlue, modifier = Modifier.weight(1f))
        StatChip(label = "Saudáveis", value = "$healthy", bgColor = CurralColors.StatusHealthyBg, textColor = CurralColors.StatusHealthy, modifier = Modifier.weight(1f))
        StatChip(label = "Prenhas", value = "$pregnant", bgColor = CurralColors.StatusPregnantBg, textColor = CurralColors.StatusPregnant, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(label: String, value: String, bgColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = bgColor) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(label, fontSize = 11.sp, color = textColor)
        }
    }
}

// ─── Search Bar ────────────────────────────────────────────────────────────────

@Composable
private fun AnimaisSearchBar(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = CurralColors.SearchBackground,
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = CurralColors.TextSecondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                decorationBox = { innerTextField: @Composable () -> Unit ->
                    Box {
                        if (query.isEmpty()) {
                            Text("Buscar por nome ou brinco", color = CurralColors.TextSecondary, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                },
            )
            Icon(Icons.Default.FilterList, contentDescription = "Filtros", tint = CurralColors.TextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}

// ─── Filter Chips ──────────────────────────────────────────────────────────────

@Composable
private fun TypeFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) CurralColors.FilterChipActiveBg else CurralColors.FilterChipBg,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) CurralColors.FilterChipActiveText else CurralColors.FilterChipText,
        )
    }
}

@Composable
private fun ViewToggleButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) CurralColors.FilterChipActiveBg else CurralColors.FilterChipBg,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) CurralColors.FilterChipActiveText else CurralColors.FilterChipText,
        )
    }
}

// ─── Animals List ──────────────────────────────────────────────────────────────

@Composable
private fun AnimalsListView(animals: List<Animal>, onAnimalClick: (String) -> Unit) {
    if (animals.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum animal encontrado", color = CurralColors.TextSecondary, fontSize = 15.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(animals, key = { it.id }) { animal ->
                AnimalListItem(animal = animal, onClick = { onAnimalClick(animal.id) })
            }
        }
    }
}

@Composable
internal fun AnimalListItem(animal: Animal, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Emoji icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CurralColors.SearchBackground),
                contentAlignment = Alignment.Center,
            ) {
                Text(animal.type.emoji, fontSize = 26.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(animal.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${animal.type.label} · ${animal.breed}",
                    fontSize = 12.sp,
                    color = CurralColors.TextSecondary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Brinco #${animal.tagNumber}",
                    fontSize = 12.sp,
                    color = CurralColors.TextSecondary,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                AnimalStatusChip(animal.status)
                Text(
                    ageLabel(animal.birthDate),
                    fontSize = 11.sp,
                    color = CurralColors.TextSecondary,
                )
            }
        }
    }
}

@Composable
internal fun AnimalStatusChip(status: AnimalStatus) {
    val (bg, fg) = statusColors(status)
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = fg,
        )
    }
}

// ─── Groups List ───────────────────────────────────────────────────────────────

@Composable
private fun GroupsListView(groups: List<AnimalGroup>, onGroupClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(groups, key = { it.id }) { group ->
            GroupCard(group = group, onClick = { onGroupClick(group.id) })
        }
    }
}

@Composable
private fun GroupCard(group: AnimalGroup, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(group.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
                if (group.description.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(group.description, fontSize = 12.sp, color = CurralColors.TextSecondary, maxLines = 2)
                }
                Spacer(Modifier.height(6.dp))
                Surface(shape = RoundedCornerShape(20.dp), color = CurralColors.StatBlueBg) {
                    Text(
                        "${group.animalIds.size} animais",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontSize = 12.sp,
                        color = CurralColors.StatBlue,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = CurralColors.TextSecondary)
        }
    }
}

// ─── Helpers ───────────────────────────────────────────────────────────────────

internal fun statusColors(status: AnimalStatus): Pair<Color, Color> = when (status) {
    AnimalStatus.SAUDAVEL -> CurralColors.StatusHealthyBg to CurralColors.StatusHealthy
    AnimalStatus.PRENHA   -> CurralColors.StatusPregnantBg to CurralColors.StatusPregnant
    AnimalStatus.DOENTE   -> CurralColors.StatusSickBg to CurralColors.StatusSick
    AnimalStatus.VENDIDO  -> CurralColors.StatusSoldBg to CurralColors.StatusSold
    AnimalStatus.MORTO    -> CurralColors.StatusDeadBg to CurralColors.StatusDead
}

internal fun ageLabel(birthDate: String): String {
    // birthDate format: "YYYY-MM-DD"
    return try {
        val parts = birthDate.split("-")
        val birthYear = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        // Approximate age using hardcoded current year/month for demo
        val currentYear = 2026
        val currentMonth = 5
        val months = (currentYear - birthYear) * 12 + (currentMonth - birthMonth)
        if (months >= 12) "${months / 12} ano${if (months / 12 > 1) "s" else ""}"
        else "$months ${if (months == 1) "mês" else "meses"}"
    } catch (_: Exception) {
        ""
    }
}
