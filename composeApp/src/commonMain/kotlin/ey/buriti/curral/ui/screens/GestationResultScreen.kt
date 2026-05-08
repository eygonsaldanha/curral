package ey.buriti.curral.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import ey.buriti.curral.data.GestationResultType
import ey.buriti.curral.platform.getCurrentDate
import ey.buriti.curral.ui.theme.CurralColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestationResultScreen(
    animalId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animal = AnimalRepository.getAnimal(animalId) ?: return
    val today = remember { getCurrentDate().toDisplayDateString() }
    var resultType by remember { mutableStateOf(GestationResultType.PARTO_CONCLUIDO) }
    var date by remember { mutableStateOf(today) }
    var notes by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar resultado", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
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
                    Text("Resultado da gestação de ${animal.name}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        GestationResultType.entries.forEach { option ->
                            val selected = option == resultType
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) CurralColors.FabGreen else CurralColors.Background,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { resultType = option },
                            ) {
                                Text(
                                    text = option.label,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    color = if (selected) Color.White else CurralColors.TextPrimary,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                )
                            }
                        }
                    }
                    OutlinedTextField(value = date, onValueChange = { date = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Data") })
                    dateError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Observações") })
                    Button(
                        onClick = {
                            val isoDate = date.toIsoDateOrNull()
                            if (isoDate == null) {
                                dateError = "Informe a data no formato DD/MM/AAAA."
                                return@Button
                            }
                            dateError = null
                            AnimalRepository.registerGestationResult(animalId, resultType, isoDate, notes)
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CurralColors.FabGreen),
                    ) {
                        Text("Salvar resultado")
                    }
                }
            }
        }
    }
}
