package fi.anttihemminki.holygrain.facedistance

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.face.Face
import fi.anttihemminki.holygrain.HOLY_TAG

interface DistanceReceiverInterface {
}

interface DistanceImageReceiverInterface : DistanceReceiverInterface {
    fun receiveFaceImage(image: ImageProxy /*, faces: MutableList<Face> */, time: Long)
}

interface DistanceDataReceiverInterface : DistanceReceiverInterface {
    fun receiveFaceData(faces: MutableList<Face>, time: Long)
}

interface DistanceImageAndDataReceiverInterface : DistanceDataReceiverInterface, DistanceImageReceiverInterface {
    fun receiveFaceImageAndData(image: ImageProxy, faces: MutableList<Face>, time: Long)
}