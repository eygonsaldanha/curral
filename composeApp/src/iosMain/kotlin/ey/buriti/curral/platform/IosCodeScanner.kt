package ey.buriti.curral.platform

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeAztecCode
import platform.AVFoundation.AVMetadataObjectTypeCode128Code
import platform.AVFoundation.AVMetadataObjectTypeCode39Code
import platform.AVFoundation.AVMetadataObjectTypeCode93Code
import platform.AVFoundation.AVMetadataObjectTypeDataMatrixCode
import platform.AVFoundation.AVMetadataObjectTypeEAN13Code
import platform.AVFoundation.AVMetadataObjectTypeEAN8Code
import platform.AVFoundation.AVMetadataObjectTypePDF417Code
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.AVFoundation.AVMetadataObjectTypeUPCECode
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIApplication
import platform.UIKit.UIButton
import platform.UIKit.UIButtonTypeSystem
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UIControlStateNormal
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosCodeScanner : CodeScanner {
    override suspend fun scan(): CodeScanResult = suspendCancellableCoroutine { continuation ->
        fun finish(result: CodeScanResult) {
            if (continuation.isActive) continuation.resume(result)
        }

        fun presentScanner() {
            dispatch_async(dispatch_get_main_queue()) {
                val presenter = topViewController(UIApplication.sharedApplication.keyWindow?.rootViewController)
                if (presenter == null) {
                    finish(CodeScanResult.Unavailable("Não foi possível abrir a câmera."))
                    return@dispatch_async
                }
                val controller = ScannerViewController(::finish)
                presenter.presentViewController(controller, animated = true, completion = null)
                continuation.invokeOnCancellation {
                    dispatch_async(dispatch_get_main_queue()) {
                        controller.dismissViewControllerAnimated(true, completion = null)
                    }
                }
            }
        }

        // A primeira criação de AVCaptureDeviceInput dispara o pedido de permissão
        // do sistema. Se o acesso estiver negado, o input não é criado e o fluxo
        // retorna para a entrada manual.
        presentScanner()
    }
}

@OptIn(ExperimentalForeignApi::class)
private class ScannerViewController(
    private val onResult: (CodeScanResult) -> Unit,
) : UIViewController(nibName = null, bundle = null) {
    private val session = AVCaptureSession()
    private var completed = false
    private val metadataDelegate = ScannerMetadataDelegate { value -> complete(CodeScanResult.Value(value)) }

    override fun viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.blackColor

        val camera = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
        val input = camera?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, error = null) as? AVCaptureDeviceInput }
        if (input == null || !session.canAddInput(input)) {
            complete(CodeScanResult.Unavailable("Câmera indisponível. Informe o código manualmente."))
            return
        }
        session.addInput(input)

        val output = AVCaptureMetadataOutput()
        if (!session.canAddOutput(output)) {
            complete(CodeScanResult.Unavailable("Leitor indisponível. Informe o código manualmente."))
            return
        }
        session.addOutput(output)
        output.setMetadataObjectsDelegate(metadataDelegate, queue = dispatch_get_main_queue())
        output.metadataObjectTypes = listOf(
            AVMetadataObjectTypeQRCode,
            AVMetadataObjectTypeEAN13Code,
            AVMetadataObjectTypeEAN8Code,
            AVMetadataObjectTypeUPCECode,
            AVMetadataObjectTypeCode128Code,
            AVMetadataObjectTypeCode39Code,
            AVMetadataObjectTypeCode93Code,
            AVMetadataObjectTypePDF417Code,
            AVMetadataObjectTypeAztecCode,
            AVMetadataObjectTypeDataMatrixCode,
        )

        val preview = AVCaptureVideoPreviewLayer(session = session)
        preview.videoGravity = AVLayerVideoGravityResizeAspectFill
        preview.frame = view.bounds
        view.layer.addSublayer(preview)

        val cancel = UIButton.buttonWithType(UIButtonTypeSystem) as UIButton
        cancel.setFrame(CGRectMake(20.0, 52.0, 110.0, 44.0))
        cancel.setTitle("Cancelar", forState = UIControlStateNormal)
        cancel.setTitleColor(UIColor.whiteColor, forState = UIControlStateNormal)
        cancel.addTarget(this, action = NSSelectorFromString("cancelScanning"), forControlEvents = UIControlEventTouchUpInside)
        view.addSubview(cancel)

        modalPresentationStyle = UIModalPresentationFullScreen
        session.startRunning()
    }

    override fun viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        (view.layer.sublayers?.firstOrNull() as? AVCaptureVideoPreviewLayer)?.frame = view.bounds
    }

    @Suppress("UNUSED")
    @ObjCAction
    fun cancelScanning() = complete(CodeScanResult.Cancelled)

    private fun complete(result: CodeScanResult) {
        if (completed) return
        completed = true
        session.stopRunning()
        dismissViewControllerAnimated(true) { onResult(result) }
    }
}

@OptIn(ExperimentalForeignApi::class)
private class ScannerMetadataDelegate(
    private val onValue: (String) -> Unit,
) : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {
    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: AVCaptureConnection,
    ) {
        val value = (didOutputMetadataObjects.firstOrNull() as? AVMetadataMachineReadableCodeObject)?.stringValue
        if (!value.isNullOrBlank()) onValue(value)
    }
}

@OptIn(ExperimentalForeignApi::class)
private tailrec fun topViewController(controller: UIViewController?): UIViewController? {
    val presented = controller?.presentedViewController ?: return controller
    return topViewController(presented)
}
