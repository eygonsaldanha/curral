package ey.buriti.curral.platform

import android.content.Context
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class GoogleCodeScanner(context: Context) : CodeScanner {
    private val scanner = GmsBarcodeScanning.getClient(context)

    override suspend fun scan(): CodeScanResult = suspendCancellableCoroutine { continuation ->
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val value = barcode.rawValue
                if (continuation.isActive) continuation.resume(
                    if (value.isNullOrBlank()) CodeScanResult.Unavailable("O código não possui conteúdo legível.")
                    else CodeScanResult.Value(value)
                )
            }
            .addOnCanceledListener { if (continuation.isActive) continuation.resume(CodeScanResult.Cancelled) }
            .addOnFailureListener { error ->
                if (continuation.isActive) continuation.resume(CodeScanResult.Unavailable(error.message ?: "Não foi possível abrir a câmera."))
            }
    }
}
