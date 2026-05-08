package ey.buriti.curral.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.data.AnimalRepository
import ey.buriti.curral.ui.theme.CurralColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalGroupDetailScreen(
    groupId: String,
    onBack: () -> Unit,
    onNavigateToAnimal: (String) -> Unit,
    onOpenBatchEvent: (String) -> Unit,
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
    val availableAnimals = AnimalRepository.getAnimalsOutsideGroup(groupId)
    var showAddAnimalDialog by remember { mutableStateOf(false) }

    ScaffoldWithGroupFab(
        groupName = group.name,
        onBack = onBack,
        onBatchEvent = { onOpenBatchEvent(groupId) },
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(group.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
                        if (group.description.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(group.description, fontSize = 12.sp, color = CurralColors.TextSecondary, maxLines = 1)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(20.dp), color = CurralColors.StatBlueBg) {
                                Text(
                                    "${animals.size} animais",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 12.sp,
                                    color = CurralColors.StatBlue,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                            OutlinedButton(onClick = { showAddAnimalDialog = true }) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Adicionar animal")
                            }
                        }
                    }
                }
            }

            item { SectionTitle("Animais do grupo") }
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
                    ElevatedCard(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            AnimalListItem(animal = animal, onClick = { onNavigateToAnimal(animal.id) })
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CurralColors.SearchBackground)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(onClick = { AnimalRepository.removeAnimalFromGroup(animal.id, groupId) }) {
                                    Text("Remover do grupo")
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(72.dp)) }
        }
    }

    if (showAddAnimalDialog) {
        AlertDialog(
            onDismissRequest = { showAddAnimalDialog = false },
            title = { Text("Adicionar animal ao grupo") },
            text = {
                if (availableAnimals.isEmpty()) {
                    Text("Todos os animais já fazem parte deste grupo.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableAnimals.forEach { animal ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("${animal.type.emoji} ${animal.name}")
                                TextButton(
                                    onClick = {
                                        AnimalRepository.addAnimalToGroup(animal.id, groupId)
                                        showAddAnimalDialog = false
                                    },
                                ) {
                                    Text("Adicionar")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddAnimalDialog = false }) {
                    Text("Fechar")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScaffoldWithGroupFab(
    groupName: String,
    onBack: () -> Unit,
    onBatchEvent: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(groupName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onBatchEvent,
                containerColor = CurralColors.FabGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Event, contentDescription = null) },
                text = { Text("Novo evento") },
            )
        },
        containerColor = CurralColors.Background,
        content = content,
    )
}
