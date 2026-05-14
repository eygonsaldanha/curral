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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
fun ManageAnimalGroupsScreen(
    animalId: String,
    onBack: () -> Unit,
    onNavigateToAnimal: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val animal = AnimalRepository.getAnimal(animalId)
    if (animal == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Animal não encontrado", color = CurralColors.TextSecondary)
        }
        return
    }

    val currentGroups = AnimalRepository.getGroupsForAnimal(animalId)
    val availableGroups = AnimalRepository.getAvailableGroupsForAnimal(animalId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grupos de ${animal.name}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        containerColor = CurralColors.Background,
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { SectionTitle("Já participa") }
            if (currentGroups.isEmpty()) {
                item { Text("Nenhum grupo vinculado.", color = CurralColors.TextSecondary) }
            } else {
                items(currentGroups, key = { it.id }) { group ->
                    ElevatedCard(shape = RoundedCornerShape(14.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(group.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
                                    if (group.description.isNotBlank()) {
                                        Text(group.description, fontSize = 12.sp, color = CurralColors.TextSecondary, maxLines = 1)
                                    }
                                }
                                TextButton(onClick = { AnimalRepository.removeAnimalFromGroup(animalId, group.id) }) {
                                    Text("Remover")
                                }
                            }
                            val otherAnimals = AnimalRepository.getAnimalsInGroup(group.id).filter { it.id != animalId }
                            if (otherAnimals.isNotEmpty()) {
                                Text("Outros animais no grupo", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = CurralColors.TextSecondary)
                                otherAnimals.forEach { other ->
                                    Text(
                                        text = "${other.type.emoji} ${other.name}",
                                        fontSize = 13.sp,
                                        color = CurralColors.TextPrimary,
                                        modifier = Modifier
                                            .padding(start = 4.dp)
                                            .clickable { onNavigateToAnimal(other.id) },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(8.dp)) }
            item { SectionTitle("Adicionar a um novo grupo") }
            if (availableGroups.isEmpty()) {
                item { Text("Não há outros grupos disponíveis.", color = CurralColors.TextSecondary) }
            } else {
                items(availableGroups, key = { it.id }) { group ->
                    ElevatedCard(shape = RoundedCornerShape(14.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(group.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
                                Text("${group.animalIds.size} animais", fontSize = 12.sp, color = CurralColors.TextSecondary)
                            }
                            TextButton(onClick = { AnimalRepository.addAnimalToGroup(animalId, group.id) }) {
                                Text("Adicionar")
                            }
                        }
                    }
                }
            }
        }
    }
}
