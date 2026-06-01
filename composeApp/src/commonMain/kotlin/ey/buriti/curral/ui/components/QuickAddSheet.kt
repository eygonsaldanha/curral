package ey.buriti.curral.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.platform.getCurrentDate
import ey.buriti.curral.model.*
import ey.buriti.curral.ui.theme.CurralColors
import ey.buriti.curral.ui.viewmodel.AnimaisViewModel
import ey.buriti.curral.ui.viewmodel.EstoqueViewModel
import ey.buriti.curral.ui.viewmodel.ProducaoViewModel
import org.koin.compose.viewmodel.koinViewModel

enum class QuickAddPage { MENU, ANIMAL, PRODUCAO, EVENTO, ESTOQUE }

data class QuickAddRequest(
    val initialPage: QuickAddPage = QuickAddPage.MENU,
    val preselectedAnimalId: String? = null,
    val preselectedGroupId: String? = null,
    val editingAnimalId: String? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    onDismiss: () -> Unit,
    request: QuickAddRequest = QuickAddRequest(),
    onAnimalSaved: (String) -> Unit = {},
) {
    var page by remember(request) { mutableStateOf(request.initialPage) }
    val onSubpageBack: () -> Unit = {
        if (request.initialPage == QuickAddPage.MENU) page = QuickAddPage.MENU else onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
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
                onBack = onSubpageBack,
                onSave = {
                    onAnimalSaved(it)
                    onDismiss()
                },
                animalId = request.editingAnimalId,
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
            )
        }
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
            MenuTile(emoji = "📊", label = "Produção", onClick = onSelectProducao, modifier = Modifier.weight(1f))
            MenuTile(emoji = "🐄", label = "Animal", onClick = onSelectAnimal, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MenuTile(emoji = "📅", label = "Evento", onClick = onSelectEvento, modifier = Modifier.weight(1f))
            MenuTile(emoji = "📦", label = "Estoque", onClick = onSelectEstoque, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MenuTile(emoji: String, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextPrimary)
        }
    }
}

// ─── Shared helpers ────────────────────────────────────────────────────────────

@Composable
internal fun FormHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CurralColors.Background)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = CurralColors.TextPrimary)
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
    var productType by remember { mutableStateOf<ProductType?>(null) }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var date by remember { mutableStateOf(getCurrentDate().toDisplayDateString()) }
    var notes by remember { mutableStateOf("") }
    var photoLabel by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productType) {
        unit = when (productType) {
            ProductType.LEITE -> "L"
            ProductType.OVOS -> "und"
            else -> "kg"
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
        FormHeader(title = "Nova Entrada de Produção", onBack = onBack)

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
                onValueChange = { date = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("DD/MM/AAAA") },
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

    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<EventType?>(null) }
    var date by remember { mutableStateOf(getCurrentDate().toDisplayDateString()) }
    var time by remember { mutableStateOf("") }
    var assignToAnimal by remember(preselectedAnimalId, preselectedGroupId) {
        mutableStateOf(preselectedAnimalId != null || preselectedGroupId == null)
    }
    var selectedAnimalId by remember(preselectedAnimalId) { mutableStateOf(preselectedAnimalId) }
    var selectedGroupId by remember(preselectedGroupId) { mutableStateOf(preselectedGroupId) }
    var description by remember { mutableStateOf("") }
    var photoLabel by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    var animalDropdownExpanded by remember { mutableStateOf(false) }
    var groupDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FormHeader(title = "Novo Evento", onBack = onBack)

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
                    onValueChange = { date = it },
                    modifier = Modifier.width(160.dp),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("DD/MM/AAAA") },
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
private fun EstoqueFormContent(onBack: () -> Unit, onSave: () -> Unit) {
    val vm: EstoqueViewModel = koinViewModel()
    var selectedCategory by remember { mutableStateOf<StockCategory?>(null) }
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var lowStockThreshold by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FormHeader(title = "Novo Item de Estoque", onBack = onBack)

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
                                Text(cat.emoji, fontSize = 20.sp)
                                Text(cat.label, fontSize = 12.sp,
                                    color = if (sel) Color.White else CurralColors.TextPrimary,
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
                                Text(cat.emoji, fontSize = 20.sp)
                                Text(cat.label, fontSize = 12.sp,
                                    color = if (sel) Color.White else CurralColors.TextPrimary,
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

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FormField("Quantidade") {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                    modifier = Modifier.width(140.dp),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
            }
            FormField("Unidade") {
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    modifier = Modifier.width(110.dp),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("kg, L, un") },
                    singleLine = true,
                )
            }
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
                onValueChange = { expiryDate = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("DD/MM/AAAA") },
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
                )
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
