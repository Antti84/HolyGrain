package fi.anttihemminki.holygrain.facedistance

import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.face.Face

interface DistanceReceiverInterface {
}

interface DistanceImageReceiverInterface : DistanceReceiverInterface {
    fun receiveFaceImage(image: ImageProxy /*, faces: MutableList<Face> */, time: Long)
}

interface DistanceDataReceiverInterface : DistanceReceiverInterface {
    fun receiveFaceData(faces: MutableList<Face>, time: Long)
}

interface DistanceImageAndDataReceiverInterface : DistanceReceiverInterface {
    fun receiveFaceImageAndData(image: ImageProxy, faces: MutableList<Face>, time: Long)
}

interface DistanceAndImageReceiverInterface : DistanceReceiverInterface {
    fun receiveDistanceAndImage(image: ImageProxy, distances: ArrayList<Double>, time: Long)
}