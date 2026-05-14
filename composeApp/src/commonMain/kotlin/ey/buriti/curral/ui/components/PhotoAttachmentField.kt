package ey.buriti.curral.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PhotoAttachmentField(
    label: String = "Adicionar Foto",
    selectedPhotoLabel: String?,
    onPhotoSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showOptions by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showOptions = true },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
    ) {
        androidx.compose.material3.Icon(Icons.Default.CameraAlt, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(selectedPhotoLabel ?: label)
    }

    if (showOptions) {
        AlertDialog(
            onDismissRequest = { showOptions = false },
            title = { Text("Adicionar foto") },
            text = { Text("Escolha como deseja anexar a foto.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPhotoSelected("Foto da câmera anexada")
                        showOptions = false
                    },
                ) {
                    Text("Câmera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onPhotoSelected(if (selectedPhotoLabel == null) "Foto da galeria anexada" else null)
                        showOptions = false
                    },
                ) {
                    Text(if (selectedPhotoLabel == null) "Galeria" else "Remover")
                }
            },
        )
    }
}
