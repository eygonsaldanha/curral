package ey.buriti.curral.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.People
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
import ey.buriti.curral.data.AnimalRepository
import ey.buriti.curral.model.*
import ey.buriti.curral.ui.theme.CurralColors

private val GestationPurple = Color(0xFF7C3AED)

@Composable
fun AnimalDetailScreen(
    animalId: String,
    onBack: () -> Unit,
    onNavigateToAnimal: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val animal = AnimalRepository.getAnimal(animalId)

    if (animal == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Animal não encontrado", color = CurralColors.TextSecondary)
        }
        return
    }

    val events = AnimalRepository.getEventsForAnimal(animalId)
    val gestation = animal.gestationId?.let { AnimalRepository.getGestation(it) }
    val mother = animal.motherId?.let { AnimalRepository.getAnimal(it) }
    val father = animal.fatherId?.let { AnimalRepository.getAnimal(it) }
    val offspring = animal.offspringIds.mapNotNull { AnimalRepository.getAnimal(it) }
    val groups = animal.groupIds.mapNotNull { AnimalRepository.getGroup(it) }

    var showAddEventDialog by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AnimalDetailTopBar(onBack = onBack)
        },
        containerColor = CurralColors.Background,
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Profile header ─────────────────────────────────────────────────
            item {
                ProfileHeaderCard(animal = animal)
            }

            // ── Info grid ──────────────────────────────────────────────────────
            item {
                InfoGridCard(animal = animal)
            }

            // ── Gestation ─────────────────────────────────────────────────────
            if (gestation != null) {
                item { GestationCard(gestation = gestation, father = father) }
            }

            // ── Groups ────────────────────────────────────────────────────────
            if (groups.isNotEmpty()) {
                item { GroupsCard(groups = groups) }
            }

            // ── Quick actions ──────────────────────────────────────────────────
            item {
                QuickActionsCard(
                    onWeightClick = { showWeightDialog = true },
                    onEventClick = { showAddEventDialog = true },
                )
            }

            // ── Parents ───────────────────────────────────────────────────────
            if (mother != null || father != null) {
                item {
                    ParentsCard(
                        mother = mother,
                        father = father,
                        onNavigateToAnimal = onNavigateToAnimal,
                    )
                }
            }

            // ── Offspring ─────────────────────────────────────────────────────
            if (offspring.isNotEmpty()) {
                item {
                    OffspringCard(offspring = offspring, onNavigateToAnimal = onNavigateToAnimal)
                }
            }

            // ── Event history ─────────────────────────────────────────────────
            item {
                EventHistoryCard(events = events)
            }

            item { Spacer(Modifier.height(8.dp)) }
        }
    }

    if (showAddEventDialog) {
        AddEventDialog(animalName = animal.name, onDismiss = { showAddEventDialog = false })
    }
    if (showWeightDialog) {
        WeightDialog(animalName = animal.name, onDismiss = { showWeightDialog = false })
    }
}

// ─── Top Bar ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimalDetailTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CurralColors.Background),
    )
}

// ─── Profile Header Card ───────────────────────────────────────────────────────

@Composable
private fun ProfileHeaderCard(animal: Animal) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0ECD8)),
                contentAlignment = Alignment.Center,
            ) {
                Text(animal.type.emoji, fontSize = 34.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    animal.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CurralColors.TextPrimary,
                )
                Text(
                    "${animal.type.label} • ${animal.breed}",
                    fontSize = 13.sp,
                    color = CurralColors.TextSecondary,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AnimalStatusChip(animal.status)
                    SexChip(animal.sex)
                }
            }
        }
    }
}

@Composable
private fun SexChip(sex: AnimalSex) {
    val (bg, fg) = when (sex) {
        AnimalSex.FEMEA -> CurralColors.SexFemaleBg to CurralColors.SexFemaleFg
        AnimalSex.MACHO -> CurralColors.SexMaleBg to CurralColors.SexMaleFg
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(
            sex.label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = fg,
        )
    }
}

// ─── Info Grid Card ────────────────────────────────────────────────────────────

@Composable
private fun InfoGridCard(animal: Animal) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoCell(
                    label = "Brinco",
                    value = "BR-${animal.tagNumber}",
                    modifier = Modifier.weight(1f),
                )
                InfoCell(
                    label = "Idade",
                    value = ageLabel(animal.birthDate),
                    modifier = Modifier.weight(1f),
                )
            }
            HorizontalDivider(
                color = CurralColors.SearchBackground,
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoCell(
                    label = "Nascimento",
                    value = formatDate(animal.birthDate),
                    modifier = Modifier.weight(1f),
                )
                InfoCell(
                    label = "Peso",
                    value = "${animal.weightKg.toInt()} kg",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun InfoCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 2.dp)) {
        Text(label, fontSize = 12.sp, color = CurralColors.TextSecondary)
        Spacer(Modifier.height(3.dp))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
    }
}

// ─── Gestation Card ────────────────────────────────────────────────────────────

@Composable
private fun GestationCard(gestation: Gestation, father: Animal?) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "🤰 Gestação em Andamento",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GestationPurple,
                )
                if (father != null) {
                    Text(
                        father.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = GestationPurple,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            GestationRow("Touro", father?.name ?: "—")
            GestationRow("Fecundação:", formatDate(gestation.startDate))
            GestationRowWithBadge("Previsão de Parto:", formatDate(gestation.expectedBirthDate))
            if (gestation.notes.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(gestation.notes, fontSize = 12.sp, color = CurralColors.TextSecondary)
            }
            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GestationPurple),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GestationPurple),
            ) {
                Text("Registrar Resultado", color = GestationPurple)
            }
        }
    }
}

@Composable
private fun GestationRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, fontSize = 13.sp, color = CurralColors.TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextPrimary)
    }
}

@Composable
private fun GestationRowWithBadge(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 13.sp, color = CurralColors.TextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextPrimary)
            Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFE8F5E9)) {
                Text(
                    "exata",
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// ─── Groups Card ───────────────────────────────────────────────────────────────

@Composable
private fun GroupsCard(groups: List<AnimalGroup>) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Grupos", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
                TextButton(onClick = {}) {
                    Text("Gerenciar", fontSize = 13.sp, color = GestationPurple)
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                groups.forEach { group ->
                    Surface(shape = RoundedCornerShape(20.dp), color = CurralColors.SearchBackground) {
                        Text(
                            group.name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = CurralColors.TextPrimary,
                        )
                    }
                }
            }
        }
    }
}

// ─── Quick Actions Card ────────────────────────────────────────────────────────

@Composable
private fun QuickActionsCard(onWeightClick: () -> Unit, onEventClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ações Rápidas", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                QuickActionButton(
                    icon = Icons.Default.MonitorWeight,
                    label = "Controle\nde Peso",
                    onClick = onWeightClick,
                    modifier = Modifier.weight(1f),
                )
                QuickActionButton(
                    icon = Icons.Default.DateRange,
                    label = "Adicionar\nEvento",
                    onClick = onEventClick,
                    modifier = Modifier.weight(1f),
                )
                QuickActionButton(
                    icon = Icons.Default.People,
                    label = "Editar\nPais",
                    onClick = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CurralColors.Background,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = CurralColors.TextPrimary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                label,
                fontSize = 11.sp,
                color = CurralColors.TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
            )
        }
    }
}

// ─── Parents Card ──────────────────────────────────────────────────────────────

@Composable
private fun ParentsCard(
    mother: Animal?,
    father: Animal?,
    onNavigateToAnimal: (String) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Pais", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                ParentCell(
                    label = "Mãe",
                    animal = mother,
                    onClick = { mother?.let { onNavigateToAnimal(it.id) } },
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(44.dp)
                        .background(CurralColors.SearchBackground),
                )
                ParentCell(
                    label = "Pai",
                    animal = father,
                    onClick = { father?.let { onNavigateToAnimal(it.id) } },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ParentCell(
    label: String,
    animal: Animal?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(enabled = animal != null, onClick = onClick)
            .padding(horizontal = 4.dp),
    ) {
        Text(label, fontSize = 12.sp, color = CurralColors.TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text(
            if (animal != null) "${animal.type.emoji} ${animal.name}" else "—",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (animal != null) CurralColors.TextPrimary else CurralColors.TextSecondary,
        )
    }
}

// ─── Offspring Card ────────────────────────────────────────────────────────────

@Composable
private fun OffspringCard(offspring: List<Animal>, onNavigateToAnimal: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Filhotes (${offspring.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CurralColors.SectionHeader,
            )
            Spacer(Modifier.height(8.dp))
            offspring.forEachIndexed { index, child ->
                if (index > 0) {
                    HorizontalDivider(color = CurralColors.SearchBackground, thickness = 0.5.dp)
                }
                OffspringRow(child = child, onClick = { onNavigateToAnimal(child.id) })
            }
        }
    }
}

@Composable
private fun OffspringRow(child: Animal, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${child.type.emoji} ${child.name}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = CurralColors.TextPrimary,
            )
            Text(
                "Nascimento: ${formatDate(child.birthDate)}",
                fontSize = 12.sp,
                color = CurralColors.StatusSick,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = CurralColors.NavInactive,
        )
    }
}

// ─── Event History Card ────────────────────────────────────────────────────────

@Composable
private fun EventHistoryCard(events: List<AnimalEvent>) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Histórico", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
            Spacer(Modifier.height(8.dp))
            if (events.isEmpty()) {
                Text(
                    "Nenhum evento registrado.",
                    color = CurralColors.TextSecondary,
                    fontSize = 14.sp,
                )
            } else {
                events.forEachIndexed { index, event ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = CurralColors.SearchBackground,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                    EventHistoryRow(event = event)
                }
            }
        }
    }
}

@Composable
private fun EventHistoryRow(event: AnimalEvent) {
    val iconBg = when (event.type) {
        EventType.NASCIMENTO, EventType.NASCIMENTO_FILHOTE -> CurralColors.StatusPregnantBg
        EventType.VACINACAO -> CurralColors.StatusHealthyBg
        EventType.TRATAMENTO -> CurralColors.StatusSickBg
        EventType.CONTROLE_PESO -> CurralColors.StatBlueBg
        else -> CurralColors.SearchBackground
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(event.type.emoji, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    event.type.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CurralColors.TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                Text(formatDate(event.date), fontSize = 12.sp, color = CurralColors.TextSecondary)
            }
            if (event.notes.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(event.notes, fontSize = 13.sp, color = CurralColors.TextSecondary)
            }
            if (event.weightKg != null) {
                Text("${event.weightKg} kg", fontSize = 13.sp, color = CurralColors.StatusHealthy)
            }
        }
    }
}

// ─── Reusable ─────────────────────────────────────────────────────────────────

@Composable
internal fun SectionTitle(title: String) {
    Text(
        title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = CurralColors.SectionHeader,
    )
}

// ─── Dialogs (stubs) ───────────────────────────────────────────────────────────

@Composable
private fun AddEventDialog(animalName: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Evento") },
        text = { Text("Registrar novo evento para $animalName.\n(Formulário completo em breve)") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
    )
}

@Composable
private fun WeightDialog(animalName: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Controle de Peso") },
        text = { Text("Registrar novo peso para $animalName.\n(Formulário completo em breve)") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
    )
}

// ─── Helpers ───────────────────────────────────────────────────────────────────

private fun formatDate(date: String): String {
    return try {
        val parts = date.split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } catch (_: Exception) {
        date
    }
}
