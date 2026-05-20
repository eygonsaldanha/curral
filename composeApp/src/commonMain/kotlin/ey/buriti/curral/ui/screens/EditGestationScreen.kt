package ey.buriti.curral.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.ui.theme.CurralColors
import ey.buriti.curral.ui.viewmodel.EditGestationViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGestationScreen(
    animalId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: EditGestationViewModel = koinViewModel { parametersOf(animalId) }
    val animal by vm.animal.collectAsState()
    val gestation by vm.gestation.collectAsState()
    val maleAnimals by vm.maleAnimals.collectAsState()
    val currentAnimal = animal ?: return
    val currentGestation = gestation ?: return

    var startDate by remember(currentGestation.id) { mutableStateOf(formatDate(currentGestation.startDate)) }
    var expectedBirthDate by remember(currentGestation.id) { mutableStateOf(formatDate(currentGestation.expectedBirthDate)) }
    var notes by remember(currentGestation.id) { mutableStateOf(currentGestation.notes) }
    var selectedFatherId by remember(currentGestation.id) { mutableStateOf(currentGestation.fatherId) }
    var expanded by remember { mutableStateOf(false) }
    var dateError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar gestação", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        containerColor = CurralColors.Background,
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Atualizar dados da gestação de ${currentAnimal.name}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
                    OutlinedTextField(value = startDate, onValueChange = { startDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Data da fecundação") })
                    OutlinedTextField(value = expectedBirthDate, onValueChange = { expectedBirthDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Previsão de parto") })
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = maleAnimals.find { it.id == selectedFatherId }?.name.orEmpty(),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            label = { Text("Pai") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            maleAnimals.forEach { male ->
                                DropdownMenuItem(
                                    text = { Text(male.name) },
                                    onClick = {
                                        selectedFatherId = male.id
                                        expanded = false
                                    },
                                )
                            }
                        }
                    }
                    dateError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Observações") })
                    Button(
                        onClick = {
                            val startIso = startDate.toIsoDateOrNull()
                            val expectedIso = expectedBirthDate.toIsoDateOrNull()
                            if (startIso == null || expectedIso == null) {
                                dateError = "Use o formato DD/MM/AAAA nas datas."
                                return@Button
                            }
                            dateError = null
                            vm.updateGestation(
                                currentGestation.copy(
                                    startDate = startIso,
                                    expectedBirthDate = expectedIso,
                                    notes = notes,
                                    fatherId = selectedFatherId,
                                ),
                            )
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CurralColors.FabGreen),
                    ) {
                        Text("Salvar alterações")
                    }
                }
            }
        }
    }
}
