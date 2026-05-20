package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.ui.theme.CurralColors
import ey.buriti.curral.ui.viewmodel.PerfilViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PerfilScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: PerfilViewModel = koinViewModel(),
) {
    val state by vm.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CurralColors.Background)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = CurralColors.TextPrimary,
                )
            }
            Text(
                "Perfil",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = CurralColors.TextPrimary,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF37474F)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    state.initials,
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                state.email.ifBlank { "Fazendeiro" },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = CurralColors.TextPrimary,
            )
            Text(
                "Fazendeiro",
                fontSize = 14.sp,
                color = CurralColors.TextSecondary,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProfileStatCard("${state.totalAnimals}", "Animais", Modifier.weight(1f))
            ProfileStatCard("${state.healthyAnimals}", "Saudáveis", Modifier.weight(1f))
            ProfileStatCard("${state.totalGroups}", "Grupos", Modifier.weight(1f))
        }

        Spacer(Modifier.height(28.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = CurralColors.Surface,
        ) {
            Column {
                ProfileOptionRow(
                    icon = Icons.Default.Edit,
                    iconColor = Color(0xFF3B82F6),
                    label = "Editar Perfil",
                    onClick = { /* TODO */ },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = CurralColors.TextSecondary.copy(alpha = 0.2f))
                ProfileOptionRow(
                    icon = Icons.Default.Notifications,
                    iconColor = Color(0xFFF59E0B),
                    label = "Preferências de Notificação",
                    onClick = { /* TODO */ },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = CurralColors.TextSecondary.copy(alpha = 0.2f))
                ProfileOptionRow(
                    icon = Icons.Default.Lock,
                    iconColor = Color(0xFF8B5CF6),
                    label = "Segurança e Acesso",
                    onClick = { /* TODO */ },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = CurralColors.TextSecondary.copy(alpha = 0.2f))
                ProfileOptionRow(
                    icon = Icons.Default.Info,
                    iconColor = Color(0xFF6B7280),
                    label = "Sobre o App",
                    onClick = { /* TODO */ },
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            color = CurralColors.Surface,
        ) {
            ProfileOptionRow(
                icon = Icons.Default.ExitToApp,
                iconColor = Color(0xFFEF4444),
                label = "Sair da conta",
                onClick = { vm.signOut() },
                labelColor = Color(0xFFEF4444),
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileStatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = CurralColors.Surface,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
            Text(label, fontSize = 12.sp, color = CurralColors.TextSecondary)
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    onClick: () -> Unit,
    labelColor: Color = CurralColors.TextPrimary,
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Text(label, fontSize = 15.sp, color = labelColor, modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = CurralColors.TextSecondary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
