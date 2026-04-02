package ey.buriti.curral.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
    ) {
        // White bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter),
            shadowElevation = 12.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(Screen.HOME, currentScreen == Screen.HOME) {
                    onScreenSelected(Screen.HOME)
                }
                BottomNavItem(Screen.ANIMAIS, currentScreen == Screen.ANIMAIS) {
                    onScreenSelected(Screen.ANIMAIS)
                }
                // Space for center FAB
                Spacer(Modifier.width(56.dp))
                BottomNavItem(Screen.PRODUCAO, currentScreen == Screen.PRODUCAO) {
                    onScreenSelected(Screen.PRODUCAO)
                }
                BottomNavItem(Screen.ESTOQUE, currentScreen == Screen.ESTOQUE) {
                    onScreenSelected(Screen.ESTOQUE)
                }
            }
        }

        // Floating center FAB
        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 4.dp)
                .size(56.dp),
            containerColor = CurralColors.FabGreen,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Adicionar", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun BottomNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (screen) {
        Screen.HOME -> Icons.Filled.Home
        Screen.ANIMAIS -> Icons.Filled.Pets
        Screen.PRODUCAO -> Icons.AutoMirrored.Filled.ShowChart
        Screen.ESTOQUE -> Icons.Filled.Inventory2
    }
    val tint = if (isSelected) CurralColors.NavActive else CurralColors.NavInactive

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = screen.label, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(2.dp))
        Text(text = screen.label, fontSize = 11.sp, color = tint)
    }
}



