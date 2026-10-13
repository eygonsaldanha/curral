package ey.buriti.curral.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import ey.buriti.curral.ui.viewmodel.AnimalFormUiState
import ey.buriti.curral.ui.viewmodel.AnimalFormViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalFormSheetContent(
    onBack: () -> Unit,
    onSave: (String) -> Unit,
    animalId: String? = null,
    initialTagNumber: String? = null,
) {
    val vm: AnimalFormViewModel = koinViewModel { parametersOf(animalId) }
    val state by vm.state.collectAsState()
    var photoLabel by remember(animalId) { mutableStateOf<String?>(null) }
    var motherExpanded by remember { mutableStateOf(false) }
    var fatherExpanded by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    LaunchedEffect(initialTagNumber, state.isLoading) {
        if (!state.isLoading && animalId == null && state.tagNumber.isBlank() && !initialTagNumber.isNullOrBlank()) {
            vm.updateTag(initialTagNumber)
        }
    }
    LaunchedEffect(state.isDirty, state.step, state.name, state.type, state.breed, state.sex, state.tagNumber,
        state.birthDate, state.weight, state.status, state.motherId, state.fatherId) {
        if (state.isDirty && !state.isEditing) vm.saveDraft()
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    Column(
        Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally).widthIn(max = 720.dp)
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FormHeader(
            title = if (state.isEditing) "Editar animal" else "Novo animal",
            onBack = {
                if (state.step > 0) vm.previous()
                else if (state.isDirty) showExitDialog = true
                else onBack()
            },
            showClose = state.step == 0,
        )
        state.errorMessage?.let { message ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(message, modifier = Modifier.padding(14.dp), fontSize = 13.sp)
            }
        }
        WizardProgress(state.step)
        Text(stepTitle(state.step), color = CurralColors.TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(stepDescription(state.step), color = CurralColors.TextSecondary)

        when (state.step) {
            0 -> IdentificationStep(state, vm)
            1 -> ConditionStep(state, vm)
            else -> LinksStep(state, vm, photoLabel, { photoLabel = it }, motherExpanded, { motherExpanded = it }, fatherExpanded, { fatherExpanded = it })
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.step > 0) {
                OutlinedButton(onClick = vm::previous, modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp)) {
                    Text("Voltar", modifier = Modifier.padding(vertical = 5.dp))
                }
            }
            Button(
                onClick = { if (state.step < 2) vm.next() else vm.save(onSave) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                enabled = !state.isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = CurralColors.FabGreen, contentColor = Color.Black),
            ) {
                if (state.isSaving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.Black)
                else Text(if (state.step < 2) "Continuar" else if (state.isEditing) "Salvar alterações" else "Cadastrar animal",
                    modifier = Modifier.padding(vertical = 5.dp), fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Sair sem concluir?") },
            text = { Text(if (state.isEditing) "As alterações não salvas serão perdidas." else "Você pode manter os dados como rascunho para continuar depois.") },
            confirmButton = {
                if (!state.isEditing) {
                    TextButton(onClick = { vm.saveDraft(); showExitDialog = false; onBack() }) { Text("Salvar rascunho") }
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { showExitDialog = false }) { Text("Continuar") }
                    TextButton(onClick = { vm.clearDraft(); showExitDialog = false; onBack() }) { Text("Limpar e sair") }
                }
            },
        )
    }
}

@Composable
private fun WizardProgress(step: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            Surface(color = if (index <= step) CurralColors.FabGreen else CurralColors.SearchBackground,
                shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f).height(5.dp)) {}
        }
    }
    Text("Etapa ${step + 1} de 3", color = CurralColors.TextSecondary, fontSize = 12.sp)
}

@Composable
private fun IdentificationStep(s: AnimalFormUiState, vm: AnimalFormViewModel) {
    FormTextField("Nome *", s.name, vm::updateName, "Ex: Mimosa", s.errors["name"])
    FormSection("Tipo *", s.errors["type"]) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AnimalType.entries.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { type -> Choice(type.label, "", s.type == type, Modifier.weight(1f)) { vm.updateType(type) } }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
    FormTextField("Raça *", s.breed, vm::updateBreed, "Nelore, Gir, Angus…", s.errors["breed"])
    FormSection("Sexo *", s.errors["sex"]) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnimalSex.entries.forEach { sex -> Choice(sex.label, "", s.sex == sex, Modifier.weight(1f)) { vm.updateSex(sex) } }
        }
    }
    FormTextField("Número do brinco *", s.tagNumber, vm::updateTag, "0001", s.errors["tag"])
}

@Composable
private fun ConditionStep(s: AnimalFormUiState, vm: AnimalFormViewModel) {
    FormSection("Status", null) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val statuses = if (s.sex == AnimalSex.MACHO) {
                listOf(AnimalStatus.SAUDAVEL, AnimalStatus.DOENTE)
            } else {
                listOf(AnimalStatus.SAUDAVEL, AnimalStatus.PRENHA, AnimalStatus.DOENTE)
            }
            statuses.forEach { status ->
                Choice(status.label, "", s.status == status, Modifier.weight(1f)) { vm.updateStatus(status) }
            }
        }
    }
    FormTextField("Data de nascimento", s.birthDate, { vm.updateBirthDate(formatDateInput(it)) }, "00/00/0000", s.errors["birthDate"])
    FormTextField("Peso (kg)", s.weight, vm::updateWeight, "500", s.errors["weight"])
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LinksStep(
    s: AnimalFormUiState,
    vm: AnimalFormViewModel,
    photoLabel: String?,
    onPhoto: (String?) -> Unit,
    motherExpanded: Boolean,
    onMotherExpanded: (Boolean) -> Unit,
    fatherExpanded: Boolean,
    onFatherExpanded: (Boolean) -> Unit,
) {
    val females = s.animals.filter { it.sex == AnimalSex.FEMEA && it.id != s.entityId() }
    val males = s.animals.filter { it.sex == AnimalSex.MACHO && it.id != s.entityId() }
    AnimalDropdown("Mãe (opcional)", females, s.motherId, motherExpanded, onMotherExpanded, vm::updateMother)
    AnimalDropdown("Pai (opcional)", males, s.fatherId, fatherExpanded, onFatherExpanded, vm::updateFather)
    FormSection("Foto (opcional)", null) {
        PhotoAttachmentField(selectedPhotoLabel = photoLabel, onPhotoSelected = onPhoto)
    }
}

private fun AnimalFormUiState.entityId(): String? = animals.firstOrNull { it.tagNumber == tagNumber && it.name == name }?.id

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimalDropdown(label: String, options: List<Animal>, selectedId: String?, expanded: Boolean,
    onExpanded: (Boolean) -> Unit, onSelected: (String?) -> Unit) {
    FormSection(label, null) {
        ExposedDropdownMenuBox(expanded, onExpandedChange = onExpanded) {
            OutlinedTextField(
                value = options.firstOrNull { it.id == selectedId }?.name.orEmpty(), onValueChange = {}, readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                placeholder = { Text("Selecionar") }, shape = RoundedCornerShape(16.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            )
            ExposedDropdownMenu(expanded, onDismissRequest = { onExpanded(false) }) {
                DropdownMenuItem({ Text("Nenhum") }, onClick = { onSelected(null); onExpanded(false) })
                options.forEach { animal -> DropdownMenuItem({ Text("${animal.type.emoji} ${animal.name}") },
                    onClick = { onSelected(animal.id); onExpanded(false) }) }
            }
        }
    }
}

@Composable
private fun FormTextField(label: String, value: String, onChange: (String) -> Unit, placeholder: String, error: String?) {
    FormSection(label, error) {
        OutlinedTextField(value, onChange, Modifier.fillMaxWidth(), placeholder = { Text(placeholder) }, singleLine = true,
            isError = error != null, shape = RoundedCornerShape(16.dp))
    }
}

@Composable
private fun FormSection(label: String, error: String?, content: @Composable () -> Unit) {
    Column {
        Text(label, color = CurralColors.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 6.dp))
        content()
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)) }
    }
}

@Composable
private fun Choice(label: String, emoji: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) CurralColors.FabGreen else CurralColors.SearchBackground,
        contentColor = if (selected) Color.Black else CurralColors.TextPrimary,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Column(Modifier.padding(vertical = 12.dp, horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (emoji.isNotBlank()) Text(emoji, fontSize = 20.sp)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun stepTitle(step: Int) = when (step) { 0 -> "Identificação"; 1 -> "Condição"; else -> "Vínculos" }
private fun stepDescription(step: Int) = when (step) {
    0 -> "Informe os dados usados para reconhecer o animal."
    1 -> "Registre a condição e as medidas atuais."
    else -> "Relacione a origem e adicione uma foto, se desejar."
}
