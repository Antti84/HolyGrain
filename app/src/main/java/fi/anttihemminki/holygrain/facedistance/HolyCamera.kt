package fi.anttihemminki.holygrain.facedistance

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import fi.anttihemminki.holygrain.CameraActivity
import java.util.concurrent.Executors

interface HolyCameraReveiceImageInterface {
    fun receiveImage(imageProxy: ImageProxy, timeStamp: Long)
}

class HolyCamera(val activity: AppCompatActivity) {
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    var imageReceiver: HolyCameraReveiceImageInterface? = null

    @SuppressLint("UnsafeExperimentalUsageError")
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer
                        { image ->
                            if(this.imageReceiver != null) {
                                val time = System.currentTimeMillis()
                                imageReceiver!!.receiveImage(image, time)
                            } else {
                                image.close()
                            }
                        })
                    }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                        activity, cameraSelector, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(activity))

    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}