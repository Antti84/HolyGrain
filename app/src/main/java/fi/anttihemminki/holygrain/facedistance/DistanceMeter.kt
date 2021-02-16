package fi.anttihemminki.holygrain.facedistance

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.face.*
import fi.anttihemminki.holygrain.FacePoint
import fi.anttihemminki.holygrain.FacePointType
import fi.anttihemminki.holygrain.holycamera.contours
import fi.anttihemminki.holygrain.holycamera.landmarks
import kotlin.math.pow
import kotlin.math.sqrt

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

    fun startCalibration(faceId: Int, progressCb: Runnable, calibrationReadyCb: Runnable,
                            distanceReceiver: DistanceAndImageReceiverInterface) {
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
                            //imageDataReceiver = distanceReceiver
                            faceDetector.receiver = object: DistanceImageAndDataReceiverInterface {
                                override fun receiveFaceImageAndData(image: ImageProxy, faces: MutableList<Face>, time: Long) {
                                    if(faces.size > 0) {
                                        val distances: ArrayList<Double>? = getFacePointDistances(faces[0])
                                        if (distances != null) {
                                            for((i, dist) in distances.withIndex()) {
                                                val kerroin = when(i) {
                                                    0 -> calibrationFactors["oa"]
                                                    1 -> calibrationFactors["od_sulku"]
                                                    2 -> calibrationFactors["od_peitto"]
                                                    3 -> calibrationFactors["os_sulku"]
                                                    4 -> calibrationFactors["os_peitto"]
                                                    else -> 0.0
                                                }
                                                distances[i] = kerroin!! / dist
                                            }
                                            distanceReceiver.receiveDistanceAndImage(image, distances, time)
                                        }
                                    }
                                }

                            }
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

    fun getFacePointDistances(face: Face): ArrayList<Double>? {
        val interestingPointIndices = arrayOf(0, 7, 18, 29, 35)

        val points = arrayListOf<PointF>()

        for(faceIndex in interestingPointIndices) {
            val p = getFacePoint(face, FaceContour.FACE, faceIndex) ?: return null
            points.add(p)
        }


        val res = arrayListOf<Double>()
        for((index, point) in interestingPointIndices.withIndex()) {
            val nextIndex = if(index < interestingPointIndices.size-1) index+1 else 0
            val p1 = points[index]
            val p2 = points[nextIndex]
            res.add(sqrt((p1.x - p2.x).toDouble().pow(2) + (p1.y - p2.y).toDouble().pow(2)))
        }
        return res
    }

    fun calculateCalibrationData() {

        val interestingPointIndices = arrayOf(0, 7, 18, 29, 35)
        val points = arrayOf(
                arrayListOf<PointF>(),
                arrayListOf<PointF>(),
                arrayListOf<PointF>(),
                arrayListOf<PointF>(),
                arrayListOf<PointF>()
        )

        for(face in calibrationFaces) {
            for((loopIndex, faceIndex) in interestingPointIndices.withIndex()) {
                points[loopIndex].add(getFacePoint(face, FaceContour.FACE, faceIndex)!!)
            }
        }

        for((i, a) in points.withIndex()) {
            var min = -1.0
            var max = -1.0
            var mean: Double
            var sum = 0.0

            for((pi, p) in a.withIndex()) {
                val ni = if(i < points.size-1) i + 1 else 0
                val dist: Double = sqrt((p.x-points[ni][pi].x).pow(2).toDouble() +
                        (p.y-points[ni][pi].y).pow(2).toDouble())
                if(min == -1.0 || dist < min) min = dist
                if(max == -1.0 || dist > max) max = dist
                sum += dist
            }
            mean = (sum-min-max) / (a.size-2)
            //val str = "$mean ($min-$max)"

            when(i) {
                0 -> calibrationFactors["oa"] = 35.0 * mean
                1 -> calibrationFactors["od_sulku"] = 35.0 * mean
                2 -> calibrationFactors["od_peitto"] = 35.0 * mean
                3 -> calibrationFactors["os_sulku"] = 35.0 * mean
                4 -> calibrationFactors["os_peitto"] = 35.0 * mean
            }
        }
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