package ey.buriti.curral

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ey.buriti.curral.navigation.Screen
import ey.buriti.curral.ui.components.CurralBottomBar
import ey.buriti.curral.ui.screens.AnimaisScreen
import ey.buriti.curral.ui.screens.EstoqueScreen
import ey.buriti.curral.ui.screens.HomeScreen
import ey.buriti.curral.ui.screens.ProducaoScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.HOME) }

        Scaffold(
            bottomBar = {
                CurralBottomBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { currentScreen = it },
                    onFabClick = { /* TODO: abrir tela de registro rápido */ }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (currentScreen) {
                    Screen.HOME -> HomeScreen()
                    Screen.ANIMAIS -> AnimaisScreen()
                    Screen.PRODUCAO -> ProducaoScreen()
                    Screen.ESTOQUE -> EstoqueScreen()
                }
            }
        }
    }
}