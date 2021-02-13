package fi.anttihemminki.holygrain.facedistance

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.face.*
import fi.anttihemminki.holygrain.FacePoint
import fi.anttihemminki.holygrain.FacePointType
import fi.anttihemminki.holygrain.holycamera.contours
import fi.anttihemminki.holygrain.holycamera.landmarks

const val TAG = "DistanceMeter"

class DistanceMeter(activity: AppCompatActivity) : HolyCameraReveiceImageInterface {
    val numFacesToCalibration = 30
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
        if(calibrating) {
            faceDetector.analyze(image, time)
        }

        if (imageDataReceiver != null) {
            when (imageDataReceiver) {
                is DistanceImageAndDataReceiverInterface -> faceDetector.analyze(image, time)
                is DistanceDataReceiverInterface -> faceDetector.analyze(image, time)
                is DistanceImageReceiverInterface ->
                    (imageDataReceiver as DistanceImageReceiverInterface).receiveFaceImage(image, time)
            }
        }
    }

    val calibrationFaces = arrayListOf<Face>()
    //lateinit var calibrationCallback: Runnable
    var calibrating = false

    fun startCalibration(faceId: Int, progressCb: Runnable, calibrationReadyCb: Runnable) {
        if(!calibrating) {
            calibrating = true
            //calibrationCallback = callback
            faceDetector.receiver = object: DistanceImageAndDataReceiverInterface {
                override fun receiveFaceImageAndData(
                    image: ImageProxy,
                    faces: MutableList<Face>,
                    time: Long
                ) {
                    when (imageDataReceiver) {
                        is DistanceImageAndDataReceiverInterface ->
                            (imageDataReceiver as DistanceImageAndDataReceiverInterface).receiveFaceImageAndData(image, faces, time)
                    }
                    for(face in faces) {
                        if(face.trackingId == null)
                            continue
                        if(face.trackingId != faceId)
                            continue

                        calibrationFaces.add(face)
                        if(calibrationFaces.size >= numFacesToCalibration) {
                            calibrating = false
                            faceDetector.receiver = imageDataReceiver
                            calculateCalibrationData()
                            calibrationReadyCb.run()
                        } else {
                            progressCb.run()
                        }
                    }
                }

            }
        }
    }

    var calibrationFactors = mutableMapOf(
        "oa" to 0.0,
        "od_sulku" to 0.0,
        "od_peitto" to 0.0,
        "os_sulku" to 0.0,
        "os_peitto" to 0.0
    )

    fun calculateCalibrationData() {
        calibrationFactors["oa"] = getFacePoint(calibrationFaces[0], FaceContour.FACE, 0)!!.x.toDouble()
        calibrationFactors["od_sulku"] = getFacePoint(calibrationFaces[0], FaceContour.FACE, 7)!!.x.toDouble()
        calibrationFactors["od_peitto"] = getFacePoint(calibrationFaces[0], FaceContour.FACE, 18)!!.x.toDouble()
        calibrationFactors["os_sulku"] = getFacePoint(calibrationFaces[0], FaceContour.FACE, 29)!!.x.toDouble()
        calibrationFactors["os_peitto"] = getFacePoint(calibrationFaces[0], FaceContour.FACE, 35)!!.x.toDouble()
    }

    //val selectedFacePoints = arrayOf(0, 7, 18, 29, 35)
    fun getFacePoint(face: Face, contour: Int, index: Int): PointF? {

        val contData = face.getContour(contour)?.points
        if (contData != null && index < contData.size) {
            return contData[index]
        }
        return null
    }
}