package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ey.buriti.curral.ui.components.AnimalFormSheetContent
import ey.buriti.curral.ui.theme.CurralColors

@Composable
fun NovoAnimalScreen(
    onBack: () -> Unit,
    onAnimalCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(containerColor = CurralColors.Background) {
        Box(modifier.fillMaxSize().background(CurralColors.Background)) {
            AnimalFormSheetContent(onBack = onBack, onSave = onAnimalCreated)
        }
    }
}
