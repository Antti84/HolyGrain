package fi.anttihemminki.holygrain.facedistance

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class HolyFaceDetector() {
    private val TAG = "FaceDetector"
    var receiver: DistanceReceiverInterface? = null

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
    fun analyze(imageProxy: ImageProxy, timeStamp: Long) {
        val ip = imageProxy
        if(receiver != null && (receiver is DistanceDataReceiverInterface || receiver is DistanceImageAndDataReceiverInterface)) {

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                detector.process(image)
                        .addOnSuccessListener { faces ->


                            if(faces != null && faces.size > 0 && receiver != null) {
                                if(receiver is DistanceImageAndDataReceiverInterface) {
                                    (receiver as DistanceImageAndDataReceiverInterface).receiveFaceImageAndData(ip, faces, timeStamp)
                                } else if(receiver is DistanceDataReceiverInterface) {
                                    imageProxy.close()
                                    (receiver as DistanceDataReceiverInterface).receiveFaceData(faces, timeStamp)
                                }
                            } else {
                                Log.i(TAG, "NONE")
                                imageProxy.close()
                            }

                        }
                        .addOnFailureListener { e ->
                            Log.i(TAG, "Error: ${e.localizedMessage}")
                            imageProxy.close()
                        }
            }
        }
    }

}