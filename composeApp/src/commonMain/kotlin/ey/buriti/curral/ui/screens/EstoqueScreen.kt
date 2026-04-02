package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.ui.theme.CurralColors

@Composable
fun EstoqueScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(CurralColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Inventory2, contentDescription = null, modifier = Modifier.size(64.dp), tint = CurralColors.TextSecondary)
            Spacer(Modifier.height(12.dp))
            Text("Estoque", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = CurralColors.TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text("Em breve", fontSize = 14.sp, color = CurralColors.TextSecondary)
        }
    }
}

