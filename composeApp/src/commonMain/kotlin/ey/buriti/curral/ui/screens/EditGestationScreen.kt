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
import androidx.compose.material3.ExposedDropdownMenu
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.data.AnimalRepository
import ey.buriti.curral.model.AnimalSex
import ey.buriti.curral.ui.theme.CurralColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGestationScreen(
    animalId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animal = AnimalRepository.getAnimal(animalId) ?: return
    val gestation = animal.gestationId?.let(AnimalRepository::getGestation) ?: return
    val maleAnimals = AnimalRepository.animals.filter { it.sex == AnimalSex.MACHO }

    var startDate by remember { mutableStateOf(formatDate(gestation.startDate)) }
    var expectedBirthDate by remember { mutableStateOf(formatDate(gestation.expectedBirthDate)) }
    var notes by remember { mutableStateOf(gestation.notes) }
    var selectedFatherId by remember { mutableStateOf(gestation.fatherId) }
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
                    Text("Atualizar dados da gestação de ${animal.name}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
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
                            AnimalRepository.updateGestation(
                                gestation.copy(
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
