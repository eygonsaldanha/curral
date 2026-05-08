package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.model.AnimalSex
import ey.buriti.curral.model.EventType
import ey.buriti.curral.model.Gestation
import ey.buriti.curral.platform.getCurrentDate
import ey.buriti.curral.ui.theme.CurralColors

private val GestationPurple = Color(0xFF7C3AED)

@Composable
fun AnimalDetailScreen(
    animalId: String,
    onBack: () -> Unit,
    onNavigateToAnimal: (String) -> Unit,
    onOpenAddEvent: (String) -> Unit,
    onOpenEditAnimal: (String) -> Unit,
    onManageGroups: (String) -> Unit,
    onRegisterGestationResult: (String) -> Unit,
    onEditGestation: (String) -> Unit,
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
    val weightHistory = AnimalRepository.getWeightHistory(animalId)
    val gestation = animal.gestationId?.let { AnimalRepository.getGestation(it) }
    val mother = animal.motherId?.let { AnimalRepository.getAnimal(it) }
    val father = animal.fatherId?.let { AnimalRepository.getAnimal(it) }
    val offspring = animal.offspringIds.mapNotNull { AnimalRepository.getAnimal(it) }
    val groups = AnimalRepository.getGroupsForAnimal(animalId)
    var showWeightDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AnimalDetailTopBar(onBack = onBack) },
        containerColor = CurralColors.Background,
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { ProfileHeaderCard(animal = animal) }
            item { InfoGridCard(animal = animal) }
            if (gestation != null) {
                item {
                    GestationCard(
                        gestation = gestation,
                        father = father,
                        onRegisterResult = { onRegisterGestationResult(animalId) },
                        onEditGestation = { onEditGestation(animalId) },
                    )
                }
            }
            item {
                GroupsCard(groups = groups, onManageGroups = { onManageGroups(animalId) })
            }
            item {
                QuickActionsCard(
                    onWeightClick = { showWeightDialog = true },
                    onEventClick = { onOpenAddEvent(animalId) },
                    onEditAnimalClick = { onOpenEditAnimal(animalId) },
                )
            }
            if (weightHistory.isNotEmpty()) {
                item { WeightHistoryCard(weightHistory) }
            }
            if (mother != null || father != null) {
                item {
                    ParentsCard(
                        mother = mother,
                        father = father,
                        onNavigateToAnimal = onNavigateToAnimal,
                    )
                }
            }
            if (offspring.isNotEmpty()) {
                item { OffspringCard(offspring = offspring, onNavigateToAnimal = onNavigateToAnimal) }
            }
            item { EventHistoryCard(events = events) }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }

    if (showWeightDialog) {
        WeightDialog(
            animal = animal,
            onDismiss = { showWeightDialog = false },
        )
    }
}

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

@Composable
private fun ProfileHeaderCard(animal: Animal) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0ECD8)),
                contentAlignment = Alignment.Center,
            ) {
                Text(animal.type.emoji, fontSize = 34.sp)
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(animal.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
                Text("${animal.type.label} • ${animal.breed}", fontSize = 13.sp, color = CurralColors.TextSecondary)
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
        AnimalSex.FEMEA -> Color(0xFFF3E5F5) to Color(0xFF9C27B0)
        AnimalSex.MACHO -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
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

@Composable
private fun InfoGridCard(animal: Animal) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoCell(label = "Brinco", value = "BR-${animal.tagNumber}", modifier = Modifier.weight(1f))
                InfoCell(label = "Idade", value = ageLabel(animal.birthDate), modifier = Modifier.weight(1f))
            }
            HorizontalDivider(color = CurralColors.SearchBackground, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoCell(label = "Nascimento", value = formatDate(animal.birthDate), modifier = Modifier.weight(1f))
                InfoCell(label = "Peso Atual", value = "${animal.weightKg.toInt()} kg", modifier = Modifier.weight(1f))
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

@Composable
private fun GestationCard(
    gestation: Gestation,
    father: Animal?,
    onRegisterResult: () -> Unit,
    onEditGestation: () -> Unit,
) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("🤰 Gestação em andamento", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GestationPurple)
                if (father != null) {
                    Text(father.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = GestationPurple)
                }
            }
            Spacer(Modifier.height(12.dp))
            GestationRow("Touro", father?.name ?: "—")
            GestationRow("Fecundação", formatDate(gestation.startDate))
            GestationRowWithBadge("Previsão de Parto", formatDate(gestation.expectedBirthDate))
            if (gestation.notes.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(gestation.notes, fontSize = 12.sp, color = CurralColors.TextSecondary)
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onEditGestation,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GestationPurple),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GestationPurple),
                ) {
                    Text("Editar gestação", color = GestationPurple)
                }
                Button(
                    onClick = onRegisterResult,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GestationPurple),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("Registrar resultado")
                }
            }
        }
    }
}

@Composable
private fun GestationRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, fontSize = 13.sp, color = CurralColors.TextSecondary)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextPrimary)
    }
}

@Composable
private fun GestationRowWithBadge(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 13.sp, color = CurralColors.TextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextPrimary)
            Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFE8F5E9)) {
                Text(
                    "ativa",
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun GroupsCard(groups: List<AnimalGroup>, onManageGroups: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Grupos", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
                TextButton(onClick = onManageGroups) {
                    Text("Gerenciar", fontSize = 13.sp, color = GestationPurple)
                }
            }
            if (groups.isEmpty()) {
                Text("Este animal ainda não participa de grupos.", fontSize = 13.sp, color = CurralColors.TextSecondary)
            } else {
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
}

@Composable
private fun QuickActionsCard(onWeightClick: () -> Unit, onEventClick: () -> Unit, onEditAnimalClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ações rápidas", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickActionButton(icon = Icons.Default.MonitorWeight, label = "Controle\nde peso", onClick = onWeightClick, modifier = Modifier.weight(1f))
                QuickActionButton(icon = Icons.Default.DateRange, label = "Adicionar\nevento", onClick = onEventClick, modifier = Modifier.weight(1f))
                QuickActionButton(icon = Icons.Default.Edit, label = "Editar\nanimal", onClick = onEditAnimalClick, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QuickActionButton(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(12.dp), color = CurralColors.Background, modifier = modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(22.dp), tint = CurralColors.TextPrimary)
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 11.sp, color = CurralColors.TextPrimary, textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun WeightHistoryCard(weightHistory: List<AnimalEvent>) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Histórico de peso", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
            Spacer(Modifier.height(8.dp))
            weightHistory.forEachIndexed { index, event ->
                if (index > 0) {
                    HorizontalDivider(color = CurralColors.SearchBackground, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(event.weightKg?.let { "${it.toInt()} kg" } ?: "—", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
                        Text(event.notes.ifBlank { "Controle de peso" }, fontSize = 12.sp, color = CurralColors.TextSecondary)
                    }
                    Text(formatDate(event.date), fontSize = 12.sp, color = CurralColors.TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun ParentsCard(mother: Animal?, father: Animal?, onNavigateToAnimal: (String) -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Pais", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                ParentCell(label = "Mãe", animal = mother, onClick = { mother?.let { onNavigateToAnimal(it.id) } }, modifier = Modifier.weight(1f))
                Box(modifier = Modifier.width(1.dp).height(44.dp).background(CurralColors.SearchBackground))
                ParentCell(label = "Pai", animal = father, onClick = { father?.let { onNavigateToAnimal(it.id) } }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ParentCell(label: String, animal: Animal?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clickable(enabled = animal != null, onClick = onClick).padding(horizontal = 4.dp)) {
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

@Composable
private fun OffspringCard(offspring: List<Animal>, onNavigateToAnimal: (String) -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Filhotes (${offspring.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
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
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("${child.type.emoji} ${child.name}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextPrimary)
            Text("Nascimento: ${formatDate(child.birthDate)}", fontSize = 12.sp, color = CurralColors.StatusSick)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(18.dp), tint = CurralColors.NavInactive)
    }
}

@Composable
private fun EventHistoryCard(events: List<AnimalEvent>) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Histórico", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
            Spacer(Modifier.height(8.dp))
            if (events.isEmpty()) {
                Text("Nenhum evento registrado.", color = CurralColors.TextSecondary, fontSize = 14.sp)
            } else {
                events.forEachIndexed { index, event ->
                    if (index > 0) {
                        HorizontalDivider(color = CurralColors.SearchBackground, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
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
        EventType.NASCIMENTO, EventType.NASCIMENTO_FILHOTE -> Color(0xFFF3E5F5)
        EventType.VACINACAO -> Color(0xFFE8F5E9)
        EventType.TRATAMENTO -> Color(0xFFFBE9E7)
        EventType.CONTROLE_PESO -> Color(0xFFE3F2FD)
        else -> CurralColors.SearchBackground
    }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(event.type.emoji, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(event.type.label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary, modifier = Modifier.weight(1f))
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

@Composable
internal fun SectionTitle(title: String) {
    Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.SectionHeader)
}

@Composable
private fun WeightDialog(animal: Animal, onDismiss: () -> Unit) {
    val today = remember { getCurrentDate().toDisplayDateString() }
    var weight by remember(animal.id) { mutableStateOf(animal.weightKg.toInt().toString()) }
    var date by remember { mutableStateOf(today) }
    var notes by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Controle de peso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Registrar novo peso para ${animal.name}.")
                androidx.compose.material3.OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = sanitizeDecimalInput(it) },
                    label = { Text("Peso (kg)") },
                    singleLine = true,
                )
                androidx.compose.material3.OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Data") },
                    singleLine = true,
                )
                androidx.compose.material3.OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observação") },
                )
                dateError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val isoDate = date.toIsoDateOrNull()
                    if (isoDate == null) {
                        dateError = "Informe a data no formato DD/MM/AAAA."
                        return@TextButton
                    }
                    dateError = null
                    AnimalRepository.saveWeightRecord(animal.id, weight.toDoubleOrNull() ?: animal.weightKg, isoDate, notes)
                    onDismiss()
                },
                enabled = weight.isNotBlank(),
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}
