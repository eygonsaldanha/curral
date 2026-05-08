package ey.buriti.curral.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.data.AnimalRepository
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalSex
import ey.buriti.curral.model.AnimalStatus
import ey.buriti.curral.model.AnimalType
import ey.buriti.curral.ui.screens.statusColors
import ey.buriti.curral.ui.theme.CurralColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalFormSheetContent(
    onBack: () -> Unit,
    onSave: (String) -> Unit,
    animalId: String? = null,
) {
    val editingAnimal = animalId?.let(AnimalRepository::getAnimal)

    var name by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.name ?: "") }
    var selectedType by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.type) }
    var breed by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.breed ?: "") }
    var selectedSex by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.sex) }
    var tagNumber by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.tagNumber ?: "") }
    var birthDate by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.birthDate?.toDisplayDate().orEmpty()) }
    var weight by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.weightKg?.toString().orEmpty()) }
    var selectedStatus by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.status ?: AnimalStatus.SAUDAVEL) }
    var selectedMotherId by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.motherId) }
    var selectedFatherId by remember(editingAnimal?.id) { mutableStateOf(editingAnimal?.fatherId) }
    var photoLabel by remember(editingAnimal?.id) { mutableStateOf<String?>(null) }

    var motherDropdownExpanded by remember { mutableStateOf(false) }
    var fatherDropdownExpanded by remember { mutableStateOf(false) }

    val femaleAnimals = AnimalRepository.animals.filter { it.sex == AnimalSex.FEMEA && it.id != animalId }
    val maleAnimals = AnimalRepository.animals.filter { it.sex == AnimalSex.MACHO && it.id != animalId }
    val canSave = name.isNotBlank() && selectedType != null && selectedSex != null && breed.isNotBlank() && tagNumber.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FormHeader(
            title = if (editingAnimal == null) "Novo Animal" else "Editar Animal",
            onBack = onBack,
        )

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

        AnimalFormField("Tipo *") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(AnimalType.VACA, AnimalType.BOI, AnimalType.CAVALO).forEach { type ->
                        AnimalTypeChip(type = type, selected = selectedType == type, onClick = { selectedType = type }, modifier = Modifier.weight(1f))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(AnimalType.BODE, AnimalType.GALINHA, AnimalType.OUTRO).forEach { type ->
                        AnimalTypeChip(type = type, selected = selectedType == type, onClick = { selectedType = type }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

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

        AnimalFormField("Sexo *") {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AnimalSex.entries.forEach { sex ->
                    val selected = sex == selectedSex
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) CurralColors.FabGreen else Color.White,
                        modifier = Modifier.weight(1f).clickable { selectedSex = sex },
                    ) {
                        Text(
                            sex.label,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .fillMaxWidth(),
                            fontSize = 14.sp,
                            color = if (selected) Color.White else CurralColors.TextPrimary,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                }
            }
        }

        AnimalFormField("Status") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(AnimalStatus.SAUDAVEL, AnimalStatus.PRENHA, AnimalStatus.DOENTE).forEach { status ->
                        val (bg, fg) = statusColors(status)
                        val selected = selectedStatus == status
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) bg else Color.White,
                            modifier = Modifier.weight(1f).clickable { selectedStatus = status },
                        ) {
                            Text(
                                status.label,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                                fontSize = 12.sp,
                                color = if (selected) fg else CurralColors.TextSecondary,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }

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
                    onValueChange = { weight = it.filter { char -> char.isDigit() || char == '.' } },
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

        AnimalFormField("Mãe (opcional)") {
            ExposedDropdownMenuBox(
                expanded = motherDropdownExpanded,
                onExpandedChange = { motherDropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = femaleAnimals.find { it.id == selectedMotherId }?.let { "${it.type.emoji} ${it.name}" }.orEmpty(),
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
                        selectedMotherId = null
                        motherDropdownExpanded = false
                    })
                    femaleAnimals.forEach { animal ->
                        DropdownMenuItem(
                            text = { Text("${animal.type.emoji} ${animal.name}") },
                            onClick = {
                                selectedMotherId = animal.id
                                motherDropdownExpanded = false
                            },
                        )
                    }
                }
            }
        }

        AnimalFormField("Pai (opcional)") {
            ExposedDropdownMenuBox(
                expanded = fatherDropdownExpanded,
                onExpandedChange = { fatherDropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = maleAnimals.find { it.id == selectedFatherId }?.let { "${it.type.emoji} ${it.name}" }.orEmpty(),
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
                        selectedFatherId = null
                        fatherDropdownExpanded = false
                    })
                    maleAnimals.forEach { animal ->
                        DropdownMenuItem(
                            text = { Text("${animal.type.emoji} ${animal.name}") },
                            onClick = {
                                selectedFatherId = animal.id
                                fatherDropdownExpanded = false
                            },
                        )
                    }
                }
            }
        }

        AnimalFormField("Foto (opcional)") {
            PhotoAttachmentField(selectedPhotoLabel = photoLabel, onPhotoSelected = { photoLabel = it })
        }

        Button(
            onClick = {
                val type = selectedType ?: return@Button
                val sex = selectedSex ?: return@Button
                val savedId = editingAnimal?.id ?: AnimalRepository.generateAnimalId()
                val updatedAnimal = Animal(
                    id = savedId,
                    name = name.trim(),
                    type = type,
                    breed = breed.trim(),
                    status = selectedStatus,
                    sex = sex,
                    tagNumber = tagNumber.trim(),
                    birthDate = parseBirthDate(birthDate),
                    weightKg = weight.toDoubleOrNull() ?: 0.0,
                    groupIds = editingAnimal?.groupIds ?: emptyList(),
                    motherId = selectedMotherId,
                    fatherId = selectedFatherId,
                    offspringIds = editingAnimal?.offspringIds ?: emptyList(),
                    gestationId = editingAnimal?.gestationId,
                )
                if (editingAnimal == null) {
                    AnimalRepository.addAnimal(updatedAnimal)
                } else {
                    AnimalRepository.updateAnimal(updatedAnimal)
                }
                onSave(savedId)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CurralColors.FabGreen),
            enabled = canSave,
        ) {
            Text(
                if (editingAnimal == null) "Cadastrar Animal" else "Salvar Alterações",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun AnimalTypeChip(
    type: AnimalType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) CurralColors.FabGreen else Color.White,
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
private fun AnimalFormField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
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

private fun parseBirthDate(ddMmYyyy: String): String {
    val parts = ddMmYyyy.trim().split("/")
    return if (parts.size == 3) {
        "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}"
    } else {
        "2026-01-01"
    }
}

private fun String.toDisplayDate(): String {
    val parts = split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else this
}
