package fi.anttihemminki.holygrain.facedistance

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import fi.anttihemminki.holygrain.HOLY_TAG

class HolyFaceDetector() {
    private val TAG = "FaceDetector"

    var detector: FaceDetector
    init {

        val highAccuracyOpts = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()

        detector = FaceDetection.getClient(highAccuracyOpts)
    }


    @SuppressLint("UnsafeExperimentalUsageError")
    fun analyze(imageProxy: ImageProxy, faceDataReceiver: (MutableList<Face>) -> Unit) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(image)
                    .addOnSuccessListener { faces ->
                        if(faces != null && faces.size > 0) {
                            faceDataReceiver(faces)
                        } else {
                            imageProxy.close()
                            Log.i(HOLY_TAG, "No faces in image")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.i(TAG, "Error: ${e.localizedMessage}")
                        imageProxy.close()
                    }
        }
    }
}