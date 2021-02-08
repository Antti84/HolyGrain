package fi.anttihemminki.holygrain.facedistance

import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy

const val TAG = "DistanceMeter"

class DistanceMeter(activity: AppCompatActivity) : HolyCameraReveiceImageInterface {
    val camera = HolyCamera(activity)
    val faceDetector = HolyFaceDetector()
    var imageDataReceiver: DistanceReceiverInterface? = null
        set(value) {
            faceDetector.receiver = value
            field = value
        }

    init {
        camera.imageReceiver = this
    }

    fun startCamera() {
        camera.startCamera()
    }

    fun shutdown() {
        camera.shutdown()
    }

    override fun receiveImage(image: ImageProxy, time: Long) {
        if (imageDataReceiver != null) {
            when (imageDataReceiver) {
                is DistanceImageAndDataReceiverInterface -> faceDetector.analyze(image, time)
                is DistanceDataReceiverInterface -> faceDetector.analyze(image, time)
                is DistanceImageReceiverInterface ->
                    (imageDataReceiver as DistanceImageReceiverInterface).receiveFaceImage(image, time)
            }
        }
    }
}