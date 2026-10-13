package ey.buriti.curral.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.platform.getCurrentDate
import ey.buriti.curral.platform.CodeScanResult
import ey.buriti.curral.platform.CodeScanner
import ey.buriti.curral.model.*
import ey.buriti.curral.ui.theme.CurralColors
import ey.buriti.curral.ui.viewmodel.AnimaisViewModel
import ey.buriti.curral.ui.viewmodel.EstoqueViewModel
import ey.buriti.curral.ui.viewmodel.ProducaoViewModel
import ey.buriti.curral.ui.viewmodel.*
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

enum class QuickAddPage { MENU, ANIMAL, PRODUCAO, EVENTO, ESTOQUE }

data class QuickAddRequest(
    val initialPage: QuickAddPage = QuickAddPage.MENU,
    val preselectedAnimalId: String? = null,
    val preselectedGroupId: String? = null,
    val editingAnimalId: String? = null,
    val initialCode: String? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    onDismiss: () -> Unit,
    request: QuickAddRequest = QuickAddRequest(),
    onAnimalSaved: (String) -> Unit = {},
) {
    var page by remember(request) { mutableStateOf(request.initialPage) }
    var showExitDialog by remember { mutableStateOf(false) }
    val leaveForm: () -> Unit = {
        if (request.initialPage == QuickAddPage.MENU) page = QuickAddPage.MENU else onDismiss()
    }
    val onSubpageBack: () -> Unit = { showExitDialog = true }

    ModalBottomSheet(
        onDismissRequest = { if (page == QuickAddPage.MENU) onDismiss() else showExitDialog = true },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        sheetGesturesEnabled = page == QuickAddPage.MENU,
        containerColor = CurralColors.Background,
    ) {
        when (page) {
            QuickAddPage.MENU -> SheetMenu(
                onSelectProducao = { page = QuickAddPage.PRODUCAO },
                onSelectAnimal = { page = QuickAddPage.ANIMAL },
                onSelectEvento = { page = QuickAddPage.EVENTO },
                onSelectEstoque = { page = QuickAddPage.ESTOQUE },
            )
            QuickAddPage.ANIMAL -> AnimalFormSheetContent(
                onBack = leaveForm,
                onSave = {
                    onAnimalSaved(it)
                    onDismiss()
                },
                animalId = request.editingAnimalId,
                initialTagNumber = request.initialCode,
            )
            QuickAddPage.PRODUCAO -> ProducaoFormContent(
                onBack = onSubpageBack,
                onSave = { onDismiss() },
            )
            QuickAddPage.EVENTO -> EventoFormContent(
                onBack = onSubpageBack,
                onSave = { onDismiss() },
                preselectedAnimalId = request.preselectedAnimalId,
                preselectedGroupId = request.preselectedGroupId,
            )
            QuickAddPage.ESTOQUE -> EstoqueFormContent(
                onBack = onSubpageBack,
                onSave = { onDismiss() },
                initialCode = request.initialCode,
            )
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Sair sem salvar?") },
            text = { Text("Você pode manter este preenchimento como rascunho ou limpar os dados.") },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; leaveForm() }) { Text("Salvar rascunho") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { showExitDialog = false }) { Text("Continuar") }
                    TextButton(onClick = {
                        when (page) {
                            QuickAddPage.ANIMAL -> FormDraftStore.animal = null
                            QuickAddPage.PRODUCAO -> FormDraftStore.production = null
                            QuickAddPage.EVENTO -> FormDraftStore.event = null
                            QuickAddPage.ESTOQUE -> FormDraftStore.stock = null
                            QuickAddPage.MENU -> Unit
                        }
                        showExitDialog = false
                        leaveForm()
                    }) { Text("Limpar e sair") }
                }
            },
        )
    }
}

// ─── Menu ──────────────────────────────────────────────────────────────────────

@Composable
private fun SheetMenu(
    onSelectProducao: () -> Unit,
    onSelectAnimal: () -> Unit,
    onSelectEvento: () -> Unit,
    onSelectEstoque: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
    ) {
        Text(
            "Adicionar",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = CurralColors.TextPrimary,
            modifier = Modifier.padding(vertical = 12.dp),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MenuTile(icon = Icons.Default.ShowChart, label = "Produção", onClick = onSelectProducao, modifier = Modifier.weight(1f))
            MenuTile(icon = Icons.Default.Pets, label = "Animal", onClick = onSelectAnimal, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MenuTile(icon = Icons.Default.Event, label = "Evento", onClick = onSelectEvento, modifier = Modifier.weight(1f))
            MenuTile(icon = Icons.Default.Inventory2, label = "Estoque", onClick = onSelectEstoque, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MenuTile(icon: ImageVector, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CurralColors.Surface,
        modifier = modifier
            .height(90.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(icon, contentDescription = null, tint = CurralColors.FabGreen, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextPrimary)
        }
    }
}

// ─── Shared helpers ────────────────────────────────────────────────────────────

@Composable
internal fun FormHeader(title: String, onBack: () -> Unit, showClose: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CurralColors.Background)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                if (showClose) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = if (showClose) "Fechar" else "Voltar",
                tint = CurralColors.TextPrimary,
            )
        }
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
    }
}

@Composable
private fun FormField(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp))
        content()
    }
}

@Composable
private fun <T> SelectChips(
    options: List<T>,
    selected: T?,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { opt ->
            val isSelected = opt == selected
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) CurralColors.FabGreen else CurralColors.Surface,
                modifier = Modifier.clickable { onSelect(opt) },
            ) {
                Text(
                    label(opt),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 13.sp,
                    color = if (isSelected) Color.White else CurralColors.TextPrimary,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}

private fun dateToIsoOrNull(dd_mm_yyyy: String): String? {
    val p = dd_mm_yyyy.trim().split("/")
    if (p.size != 3) return null
    val day = p[0].toIntOrNull() ?: return null
    val month = p[1].toIntOrNull() ?: return null
    val year = p[2].toIntOrNull() ?: return null
    if (day !in 1..31 || month !in 1..12 || year < 1900) return null
    return "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
}

// ─── Produção Form ─────────────────────────────────────────────────────────────

@Composable
private fun ProducaoFormContent(onBack: () -> Unit, onSave: () -> Unit) {
    val vm: ProducaoViewModel = koinViewModel()
    val draft = remember { FormDraftStore.production }
    var productType by remember { mutableStateOf(draft?.productType) }
    var quantity by remember { mutableStateOf(draft?.quantity.orEmpty()) }
    var unit by remember { mutableStateOf(draft?.unit ?: "kg") }
    var date by remember { mutableStateOf(draft?.date ?: getCurrentDate().toDisplayDateString()) }
    var notes by remember { mutableStateOf(draft?.notes.orEmpty()) }
    var photoLabel by remember { mutableStateOf(draft?.photoLabel) }
    var dateError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productType) {
        unit = when (productType) {
            ProductType.LEITE -> "L"
            ProductType.OVOS -> "und"
            else -> "kg"
        }
    }
    LaunchedEffect(productType, quantity, unit, date, notes, photoLabel) {
        if (productType != null || quantity.isNotBlank() || notes.isNotBlank() || photoLabel != null) {
            FormDraftStore.production = ProductionFormDraft(productType, quantity, unit, date, notes, photoLabel)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FormHeader(title = "Nova Entrada de Produção", onBack = onBack, showClose = true)

        FormField("Tipo de Produto") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(ProductType.LEITE, ProductType.OVOS).forEach { pt ->
                        val sel = pt == productType
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (sel) CurralColors.FabGreen else CurralColors.Surface,
                            modifier = Modifier.weight(1f).clickable { productType = pt },
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(pt.emoji, fontSize = 22.sp)
                                Text(pt.label, fontSize = 13.sp,
                                    color = if (sel) Color.White else CurralColors.TextPrimary,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(ProductType.MEL, ProductType.CAPIM_FENO).forEach { pt ->
                        val sel = pt == productType
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (sel) CurralColors.FabGreen else CurralColors.Surface,
                            modifier = Modifier.weight(1f).clickable { productType = pt },
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(pt.emoji, fontSize = 22.sp)
                                Text(pt.label, fontSize = 13.sp,
                                    color = if (sel) Color.White else CurralColors.TextPrimary,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormField("Quantidade") {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() || c == '.' } },
                    modifier = Modifier.width(140.dp),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
            }
            FormField("Unidade") {
                SelectChips(
                    options = listOf("L", "kg", "und"),
                    selected = unit,
                    label = { it },
                    onSelect = { unit = it },
                )
            }
        }

        FormField("Data") {
            OutlinedTextField(
                value = date,
                onValueChange = { date = formatDateInput(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("00/00/0000") },
                trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = CurralColors.NavInactive) },
                singleLine = true,
            )
        }

        dateError?.let {
            Text(it, fontSize = 12.sp, color = Color.Red)
        }

        FormField("Foto (opcional)") {
            PhotoAttachmentField(selectedPhotoLabel = photoLabel, onPhotoSelected = { photoLabel = it })
        }

        FormField("Observação (opcional)") {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth().height(90.dp),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Observações...") },
            )
        }

        Button(
            onClick = {
                val pt = productType ?: return@Button
                val qty = quantity.toDoubleOrNull() ?: return@Button
                val isoDate = dateToIsoOrNull(date)
                if (isoDate == null) {
                    dateError = "Use o formato DD/MM/AAAA na data."
                    return@Button
                }
                dateError = null
                vm.addEntry(
                    productType = pt,
                    quantity = qty,
                    unit = unit,
                    date = isoDate,
                    notes = notes,
                )
                FormDraftStore.production = null
                onSave()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CurralColors.FabGreen),
            enabled = productType != null && quantity.isNotBlank(),
        ) {
            Text("Salvar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── Evento Form ───────────────────────────────────────────────────────────────

private val EventoTypes = listOf(
    EventType.VISITA_VETERINARIA,
    EventType.VACINACAO,
    EventType.ALIMENTACAO_ESPECIAL,
    EventType.OUTRO,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventoFormContent(
    onBack: () -> Unit,
    onSave: () -> Unit,
    preselectedAnimalId: String? = null,
    preselectedGroupId: String? = null,
) {
    val vm: AnimaisViewModel = koinViewModel()
    val animals by vm.animals.collectAsState()
    val groups by vm.groups.collectAsState()

    val draft = remember { FormDraftStore.event }
    var title by remember { mutableStateOf(draft?.title.orEmpty()) }
    var selectedType by remember { mutableStateOf(draft?.type) }
    var date by remember { mutableStateOf(draft?.date ?: getCurrentDate().toDisplayDateString()) }
    var time by remember { mutableStateOf(draft?.time.orEmpty()) }
    var assignToAnimal by remember(preselectedAnimalId, preselectedGroupId) {
        mutableStateOf(draft?.assignToAnimal ?: (preselectedAnimalId != null || preselectedGroupId == null))
    }
    var selectedAnimalId by remember(preselectedAnimalId) { mutableStateOf(preselectedAnimalId ?: draft?.animalId) }
    var selectedGroupId by remember(preselectedGroupId) { mutableStateOf(preselectedGroupId ?: draft?.groupId) }
    var description by remember { mutableStateOf(draft?.description.orEmpty()) }
    var photoLabel by remember { mutableStateOf(draft?.photoLabel) }
    var formError by remember { mutableStateOf<String?>(null) }

    var animalDropdownExpanded by remember { mutableStateOf(false) }
    var groupDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(title, selectedType, date, time, assignToAnimal, selectedAnimalId, selectedGroupId, description, photoLabel) {
        if (title.isNotBlank() || selectedType != null || time.isNotBlank() || description.isNotBlank() || photoLabel != null) {
            FormDraftStore.event = EventFormDraft(
                title, selectedType, date, time, assignToAnimal, selectedAnimalId, selectedGroupId, description, photoLabel,
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FormHeader(title = "Novo Evento", onBack = onBack, showClose = true)

        FormField("Título") {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Ex: Vacinação de rotina") },
                singleLine = true,
            )
        }

        FormField("Tipo") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EventoTypes.take(2).forEach { et ->
                        val sel = et == selectedType
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) CurralColors.FabGreen else CurralColors.Surface,
                            modifier = Modifier.weight(1f).clickable { selectedType = et },
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(et.emoji, fontSize = 16.sp)
                                Text(et.label, fontSize = 12.sp,
                                    color = if (sel) Color.White else CurralColors.TextPrimary,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EventoTypes.drop(2).forEach { et ->
                        val sel = et == selectedType
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) CurralColors.FabGreen else CurralColors.Surface,
                            modifier = Modifier.weight(1f).clickable { selectedType = et },
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(et.emoji, fontSize = 16.sp)
                                Text(et.label, fontSize = 12.sp,
                                    color = if (sel) Color.White else CurralColors.TextPrimary,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormField("Data") {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = formatDateInput(it) },
                    modifier = Modifier.width(160.dp),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("00/00/0000") },
                    singleLine = true,
                )
            }
            FormField("Hora") {
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    modifier = Modifier.width(110.dp),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("HH:MM") },
                    singleLine = true,
                )
            }
        }

        FormField("Atribuir a") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectChips(
                    options = listOf(true, false),
                    selected = assignToAnimal,
                    label = { if (it) "Animal" else "Grupo" },
                    onSelect = {
                        assignToAnimal = it
                        selectedAnimalId = null
                        selectedGroupId = null
                    },
                )
            }
        }

        if (assignToAnimal) {
            FormField("Animal") {
                ExposedDropdownMenuBox(
                    expanded = animalDropdownExpanded,
                    onExpandedChange = { animalDropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        value = animals.find { it.id == selectedAnimalId }?.let { "${it.type.emoji} ${it.name}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("Selecionar animal") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = animalDropdownExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = animalDropdownExpanded,
                        onDismissRequest = { animalDropdownExpanded = false },
                    ) {
                        animals.forEach { animal ->
                            DropdownMenuItem(
                                text = { Text("${animal.type.emoji} ${animal.name}") },
                                onClick = {
                                    selectedAnimalId = animal.id
                                    animalDropdownExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        } else {
            FormField("Grupo") {
                ExposedDropdownMenuBox(
                    expanded = groupDropdownExpanded,
                    onExpandedChange = { groupDropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        value = groups.find { it.id == selectedGroupId }?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("Selecionar grupo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupDropdownExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = groupDropdownExpanded,
                        onDismissRequest = { groupDropdownExpanded = false },
                    ) {
                        groups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group.name) },
                                onClick = {
                                    selectedGroupId = group.id
                                    groupDropdownExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }

        FormField("Descrição (opcional)") {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth().height(90.dp),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Descrição do evento...") },
            )
        }

        FormField("Foto (opcional)") {
            PhotoAttachmentField(selectedPhotoLabel = photoLabel, onPhotoSelected = { photoLabel = it })
        }

        formError?.let {
            Text(it, fontSize = 12.sp, color = Color.Red)
        }

        Button(
            onClick = {
                val type = selectedType ?: return@Button
                val isoDate = dateToIsoOrNull(date)
                if (isoDate == null) {
                    formError = "Use o formato DD/MM/AAAA na data."
                    return@Button
                }

                val groupId = if (!assignToAnimal) selectedGroupId else null
                val animalId = if (assignToAnimal) {
                    selectedAnimalId ?: return@Button
                } else {
                    val selectedGroup = groups.firstOrNull { it.id == groupId }
                    val fallbackAnimalId = selectedGroup?.animalIds?.firstOrNull()
                    if (fallbackAnimalId == null) {
                        formError = "O grupo selecionado não possui animais vinculados."
                        return@Button
                    }
                    fallbackAnimalId
                }

                formError = null
                vm.addEvent(
                    type = type,
                    date = isoDate,
                    time = time,
                    notes = formatEventNotes(title, description),
                    animalId = animalId,
                    groupId = groupId,
                )
                FormDraftStore.event = null
                onSave()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CurralColors.FabGreen),
            enabled = selectedType != null && title.isNotBlank() &&
                    (if (assignToAnimal) selectedAnimalId != null else selectedGroupId != null),
        ) {
            Text("Salvar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun formatEventNotes(title: String, description: String): String {
    return listOf(title.trim(), description.trim())
        .filter { it.isNotBlank() }
        .joinToString(" — ")
}

// ─── Estoque Form ──────────────────────────────────────────────────────────────

@Composable
private fun EstoqueFormContent(onBack: () -> Unit, onSave: () -> Unit, initialCode: String? = null) {
    val vm: EstoqueViewModel = koinViewModel()
    val codeScanner: CodeScanner = koinInject()
    val scope = rememberCoroutineScope()
    val draft = remember { FormDraftStore.stock }
    var selectedCategory by remember { mutableStateOf(draft?.category) }
    var name by remember { mutableStateOf(draft?.name.orEmpty()) }
    var code by remember(initialCode) { mutableStateOf(initialCode ?: draft?.code.orEmpty()) }
    var quantity by remember { mutableStateOf(draft?.quantity.orEmpty()) }
    var unit by remember { mutableStateOf(draft?.unit.orEmpty()) }
    var expiryDate by remember { mutableStateOf(draft?.expiryDate.orEmpty()) }
    var lowStockThreshold by remember { mutableStateOf(draft?.lowStockThreshold.orEmpty()) }
    var notes by remember { mutableStateOf(draft?.notes.orEmpty()) }
    var formError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedCategory, name, code, quantity, unit, expiryDate, lowStockThreshold, notes) {
        if (selectedCategory != null || name.isNotBlank() || code.isNotBlank() || quantity.isNotBlank() ||
            unit.isNotBlank() || expiryDate.isNotBlank() || lowStockThreshold.isNotBlank() || notes.isNotBlank()) {
            FormDraftStore.stock = StockFormDraft(
                selectedCategory, name, code, quantity, unit, expiryDate, lowStockThreshold, notes,
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FormHeader(title = "Novo Item de Estoque", onBack = onBack, showClose = true)

        FormField("Categoria") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StockCategory.entries.take(2).forEach { cat ->
                        val sel = cat == selectedCategory
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) CurralColors.FabGreen else CurralColors.Surface,
                            modifier = Modifier.weight(1f).clickable { selectedCategory = cat },
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(stockCategoryIcon(cat), contentDescription = null, tint = if (sel) Color.Black else CurralColors.FabGreen)
                                Text(cat.label, fontSize = 12.sp,
                                    color = if (sel) Color.Black else CurralColors.TextPrimary,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StockCategory.entries.drop(2).forEach { cat ->
                        val sel = cat == selectedCategory
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) CurralColors.FabGreen else CurralColors.Surface,
                            modifier = Modifier.weight(1f).clickable { selectedCategory = cat },
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(stockCategoryIcon(cat), contentDescription = null, tint = if (sel) Color.Black else CurralColors.FabGreen)
                                Text(cat.label, fontSize = 12.sp,
                                    color = if (sel) Color.Black else CurralColors.TextPrimary,
                                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }

        FormField("Nome do Item") {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Ex: Ração para Bovinos") },
                singleLine = true,
            )
        }

        FormField("Código (opcional)") {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("QR, código de barras ou identificação interna") },
                trailingIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            when (val result = codeScanner.scan()) {
                                is CodeScanResult.Value -> code = result.rawValue
                                is CodeScanResult.Unavailable -> formError = result.reason
                                CodeScanResult.Cancelled -> Unit
                            }
                        }
                    }) { Icon(Icons.Default.QrCodeScanner, contentDescription = "Ler QR code ou código de barras") }
                },
                singleLine = true,
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) { FormField("Quantidade") {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
            } }
            Column(Modifier.weight(1f)) { FormField("Unidade") {
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("kg, L, un") },
                    singleLine = true,
                )
            } }
        }

        FormField("Nível mínimo (opcional)") {
            OutlinedTextField(
                value = lowStockThreshold,
                onValueChange = { lowStockThreshold = it.filter { c -> c.isDigit() } },
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Ex: 10") },
                singleLine = true,
            )
        }

        FormField("Data de Vencimento (opcional)") {
            OutlinedTextField(
                value = expiryDate,
                onValueChange = { expiryDate = formatDateInput(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("00/00/0000") },
                trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = CurralColors.NavInactive) },
                singleLine = true,
            )
        }

        FormField("Observação (opcional)") {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Observações...") },
            )
        }

        formError?.let {
            Text(it, fontSize = 12.sp, color = Color.Red)
        }

        Button(
            onClick = {
                val cat = selectedCategory ?: return@Button
                val qty = quantity.toIntOrNull() ?: return@Button
                val expiryIso = if (expiryDate.isBlank()) null else dateToIsoOrNull(expiryDate)
                if (expiryDate.isNotBlank() && expiryIso == null) {
                    formError = "Use o formato DD/MM/AAAA na data de vencimento."
                    return@Button
                }
                formError = null
                vm.addItem(
                    name = name.trim(),
                    category = cat,
                    quantity = qty,
                    unit = unit.ifBlank { "un" },
                    expiryDate = expiryIso,
                    lowStockThreshold = lowStockThreshold.toIntOrNull() ?: 0,
                    code = code.trim().ifBlank { null },
                )
                FormDraftStore.stock = null
                onSave()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CurralColors.FabGreen),
            enabled = selectedCategory != null && name.isNotBlank() && quantity.isNotBlank(),
        ) {
            Text("Salvar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun stockCategoryIcon(category: StockCategory): ImageVector = when (category) {
    StockCategory.RACAO -> Icons.Default.Inventory2
    StockCategory.FERRAMENTAS -> Icons.Default.Build
    StockCategory.REMEDIOS -> Icons.Default.Medication
    StockCategory.OUTROS -> Icons.Default.Category
}
