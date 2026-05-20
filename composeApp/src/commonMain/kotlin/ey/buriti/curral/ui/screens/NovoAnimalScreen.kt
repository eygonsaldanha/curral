package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.model.*
import ey.buriti.curral.ui.theme.CurralColors
import ey.buriti.curral.ui.viewmodel.AnimaisViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovoAnimalScreen(
    onBack: () -> Unit,
    onAnimalCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
    vm: AnimaisViewModel = koinViewModel(),
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<AnimalType?>(null) }
    var breed by remember { mutableStateOf("") }
    var selectedSex by remember { mutableStateOf<AnimalSex?>(null) }
    var tagNumber by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(AnimalStatus.SAUDAVEL) }

    var motherDropdownExpanded by remember { mutableStateOf(false) }
    var fatherDropdownExpanded by remember { mutableStateOf(false) }
    var selectedMotherId by remember { mutableStateOf<String?>(null) }
    var selectedFatherId by remember { mutableStateOf<String?>(null) }

    val allAnimals by vm.animals.collectAsState()
    val femaleAnimals = allAnimals.filter { it.sex == AnimalSex.FEMEA }
    val maleAnimals = allAnimals.filter { it.sex == AnimalSex.MACHO }

    val canSave = name.isNotBlank() && selectedType != null && selectedSex != null &&
            breed.isNotBlank() && tagNumber.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Animal", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CurralColors.Surface),
            )
        },
        containerColor = CurralColors.Background,
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {

                // ── Nome ──────────────────────────────────────────────────────
                AnimalFormField("Nome *") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("Ex: Mimosa") },
                        singleLine = true,
                    )
                }

                // ── Tipo ──────────────────────────────────────────────────────
                AnimalFormField("Tipo *") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(AnimalType.VACA, AnimalType.BOI, AnimalType.CAVALO).forEach { t ->
                                AnimalTypeChip(type = t, selected = selectedType == t,
                                    onClick = { selectedType = t }, modifier = Modifier.weight(1f))
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(AnimalType.BODE, AnimalType.GALINHA, AnimalType.OUTRO).forEach { t ->
                                AnimalTypeChip(type = t, selected = selectedType == t,
                                    onClick = { selectedType = t }, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // ── Raça ──────────────────────────────────────────────────────
                AnimalFormField("Raça *") {
                    OutlinedTextField(
                        value = breed,
                        onValueChange = { breed = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("Ex: Nelore, Gir, Angus...") },
                        singleLine = true,
                    )
                }

                // ── Sexo ──────────────────────────────────────────────────────
                AnimalFormField("Sexo *") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AnimalSex.entries.forEach { sex ->
                            val sel = sex == selectedSex
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (sel) CurralColors.FabGreen else CurralColors.Surface,
                                modifier = Modifier.weight(1f).clickable { selectedSex = sex },
                            ) {
                                Text(
                                    sex.label,
                                    modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth()
                                        .wrapContentWidth(Alignment.CenterHorizontally),
                                    fontSize = 14.sp,
                                    color = if (sel) Color.White else CurralColors.TextPrimary,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                )
                            }
                        }
                    }
                }

                // ── Status ────────────────────────────────────────────────────
                AnimalFormField("Status") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(AnimalStatus.SAUDAVEL, AnimalStatus.PRENHA, AnimalStatus.DOENTE).forEach { st ->
                                val (bg, fg) = statusColors(st)
                                val sel = selectedStatus == st
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (sel) bg else CurralColors.Surface,
                                    modifier = Modifier.weight(1f).clickable { selectedStatus = st },
                                ) {
                                    Text(
                                        st.label,
                                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                                            .wrapContentWidth(Alignment.CenterHorizontally),
                                        fontSize = 12.sp,
                                        color = if (sel) fg else CurralColors.TextSecondary,
                                        fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Nº Brinco + Nascimento + Peso ─────────────────────────────
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnimalFormField("Nº Brinco *", modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = tagNumber,
                            onValueChange = { tagNumber = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("0001") },
                            singleLine = true,
                        )
                    }
                    AnimalFormField("Peso (kg)", modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("500") },
                            singleLine = true,
                        )
                    }
                }

                AnimalFormField("Data de Nascimento") {
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("DD/MM/AAAA") },
                        singleLine = true,
                    )
                }

                // ── Mãe ───────────────────────────────────────────────────────
                AnimalFormField("Mãe (opcional)") {
                    ExposedDropdownMenuBox(
                        expanded = motherDropdownExpanded,
                        onExpandedChange = { motherDropdownExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = femaleAnimals.find { it.id == selectedMotherId }
                                ?.let { "${it.type.emoji} ${it.name}" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("Selecionar mãe") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = motherDropdownExpanded) },
                        )
                        ExposedDropdownMenu(
                            expanded = motherDropdownExpanded,
                            onDismissRequest = { motherDropdownExpanded = false },
                        ) {
                            DropdownMenuItem(text = { Text("Nenhuma") }, onClick = {
                                selectedMotherId = null; motherDropdownExpanded = false
                            })
                            femaleAnimals.forEach { animal ->
                                DropdownMenuItem(
                                    text = { Text("${animal.type.emoji} ${animal.name}") },
                                    onClick = { selectedMotherId = animal.id; motherDropdownExpanded = false },
                                )
                            }
                        }
                    }
                }

                // ── Pai ───────────────────────────────────────────────────────
                AnimalFormField("Pai (opcional)") {
                    ExposedDropdownMenuBox(
                        expanded = fatherDropdownExpanded,
                        onExpandedChange = { fatherDropdownExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = maleAnimals.find { it.id == selectedFatherId }
                                ?.let { "${it.type.emoji} ${it.name}" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("Selecionar pai") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fatherDropdownExpanded) },
                        )
                        ExposedDropdownMenu(
                            expanded = fatherDropdownExpanded,
                            onDismissRequest = { fatherDropdownExpanded = false },
                        ) {
                            DropdownMenuItem(text = { Text("Nenhum") }, onClick = {
                                selectedFatherId = null; fatherDropdownExpanded = false
                            })
                            maleAnimals.forEach { animal ->
                                DropdownMenuItem(
                                    text = { Text("${animal.type.emoji} ${animal.name}") },
                                    onClick = { selectedFatherId = animal.id; fatherDropdownExpanded = false },
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = {
                        val type = selectedType ?: return@Button
                        val sex = selectedSex ?: return@Button
                        val birth = parseBirthDate(birthDate)
                        vm.addAnimalFromForm(
                            name = name.trim(),
                            type = type,
                            breed = breed.trim(),
                            status = selectedStatus,
                            sex = sex,
                            tagNumber = tagNumber.trim(),
                            birthDate = birth,
                            weightKg = weight.toDoubleOrNull() ?: 0.0,
                            motherId = selectedMotherId,
                            fatherId = selectedFatherId,
                        ) { newId -> onAnimalCreated(newId) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CurralColors.FabGreen),
                    enabled = canSave,
                ) {
                    Text("Cadastrar Animal", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ─── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun AnimalTypeChip(
    type: AnimalType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) CurralColors.FabGreen else CurralColors.Surface,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(type.emoji, fontSize = 18.sp)
            Text(
                type.label,
                fontSize = 11.sp,
                color = if (selected) Color.White else CurralColors.TextPrimary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun AnimalFormField(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = CurralColors.TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        content()
    }
}

private fun parseBirthDate(dd_mm_yyyy: String): String {
    val p = dd_mm_yyyy.trim().split("/")
    return if (p.size == 3) "${p[2]}-${p[1].padStart(2,'0')}-${p[0].padStart(2,'0')}" else "2026-01-01"
}
