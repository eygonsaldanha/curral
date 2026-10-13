package ey.buriti.curral.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.navigation.Screen
import ey.buriti.curral.ui.theme.CurralColors

@Composable
fun CurralBottomBar(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    onFabClick: () -> Unit,
    onSearchClick: () -> Unit,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            .widthIn(max = 720.dp).padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            color = CurralColors.Surface.copy(alpha = 0.78f),
            contentColor = CurralColors.TextPrimary,
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, CurralColors.NavInactive.copy(alpha = 0.35f)),
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth().height(54.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.weight(1f).fillMaxHeight().clickable(onClick = onSearchClick).padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = CurralColors.FabGreen)
                    Spacer(Modifier.width(12.dp))
                    Text("Buscar animais e estoque", fontSize = 16.sp, color = CurralColors.TextSecondary)
                }
                IconButton(onClick = onScanClick, modifier = Modifier.padding(end = 8.dp)) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Ler QR code ou código de barras", tint = CurralColors.FabGreen)
                }
            }
        }
        Surface(
            color = CurralColors.Surface.copy(alpha = 0.78f),
            shape = RoundedCornerShape(34.dp),
            border = BorderStroke(1.dp, CurralColors.NavInactive.copy(alpha = 0.3f)),
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth().height(68.dp),
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DockItem(Icons.Default.Home, "Início", currentScreen == Screen.HOME) { onScreenSelected(Screen.HOME) }
                DockItem(Icons.Default.History, "Atividade", currentScreen == Screen.ACTIVITY) { onScreenSelected(Screen.ACTIVITY) }
                Surface(
                    color = CurralColors.FabGreen,
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp).clickable(onClick = onFabClick),
                ) { Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, "Adicionar", Modifier.size(29.dp)) } }
            }
        }
    }
}

@Composable
private fun DockItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(94.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = CircleShape,
            color = if (selected) CurralColors.FabGreen.copy(alpha = 0.14f) else Color.Transparent,
            modifier = Modifier.size(42.dp).clickable(onClick = onClick),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = if (selected) CurralColors.NavActive else CurralColors.NavInactive)
            }
        }
        Text(label, fontSize = 11.sp, color = if (selected) CurralColors.NavActive else CurralColors.NavInactive)
    }
}
