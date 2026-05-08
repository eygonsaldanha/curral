package ey.buriti.curral.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.data.AnimalRepository
import ey.buriti.curral.ui.theme.CurralColors

@Composable
fun AnimalGroupDetailScreen(
    groupId: String,
    onBack: () -> Unit,
    onNavigateToAnimal: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val group = AnimalRepository.getGroup(groupId)

    if (group == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Grupo não encontrado", color = CurralColors.TextSecondary)
        }
        return
    }

    val animals = AnimalRepository.getAnimalsInGroup(groupId)
    var showBatchEventDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(group.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CurralColors.Surface),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showBatchEventDialog = true },
                containerColor = CurralColors.FabGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Event, contentDescription = null) },
                text = { Text("Registrar Evento") },
            )
        },
        containerColor = CurralColors.Background,
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // ── Group info ─────────────────────────────────────────────────────
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CurralColors.Surface,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (group.description.isNotBlank()) {
                            Text(group.description, fontSize = 14.sp, color = CurralColors.TextSecondary)
                            Spacer(Modifier.height(8.dp))
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = CurralColors.StatBlueBg) {
                            Text(
                                "${animals.size} animais",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                color = CurralColors.StatBlue,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            // ── Animals list ───────────────────────────────────────────────────
            item {
                SectionTitle("Animais do Grupo")
            }
            if (animals.isEmpty()) {
                item {
                    Text(
                        "Nenhum animal neste grupo.",
                        color = CurralColors.TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            } else {
                items(animals, key = { it.id }) { animal ->
                    AnimalListItem(animal = animal, onClick = { onNavigateToAnimal(animal.id) })
                }
            }

            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showBatchEventDialog) {
        AlertDialog(
            onDismissRequest = { showBatchEventDialog = false },
            title = { Text("Registrar Evento em Lote") },
            text = {
                Text(
                    "Registrar um evento para todos os ${animals.size} animais do grupo \"${group.name}\".\n\n(Formulário completo em breve)",
                )
            },
            confirmButton = {
                Button(
                    onClick = { showBatchEventDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CurralColors.FabGreen),
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showBatchEventDialog = false }) { Text("Cancelar") } },
        )
    }
}
