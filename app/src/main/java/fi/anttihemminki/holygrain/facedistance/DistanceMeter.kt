package fi.anttihemminki.holygrain.facedistance

import android.graphics.PointF
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.android.gms.vision.face.Contour
import com.google.mlkit.vision.face.*
import kotlin.math.pow
import kotlin.math.sqrt

const val TAG = "DistanceMeter"


data class RawFaceData(val imageProxy: ImageProxy, val faces: MutableList<Face>,
                       val timeStamp: Long)
interface FaceMeter {
    fun measure(face: Face): Double
}

val rotationLimits = -15..15
fun isPostureCorrect(face: Face): Boolean {
    if(face.headEulerAngleX.toInt() !in rotationLimits) return false
    if(face.headEulerAngleY.toInt() !in rotationLimits) return false
    return true
}

const val calibrationDistance = 30.0

class DistanceMeter() {
    val numFacesToCalibration = 30

    var referenceDistances = DoubleArray(faceConnections.size)
    var calibrated = false

    val defaultMeasurer = object : FaceMeter {
        override fun measure(face: Face): Double {
            if(!calibrated) return -2.0
            if(!isPostureCorrect(face)) return -1.0

            val points = FaceData(face)
            if(!points.isOk)
                return -1.0
            val distances = ArrayList<Double>()
            for((index, connection) in faceConnections.withIndex()) {
                val p1 = points.facePoints!![connection[0]]
                val p2 = points.facePoints!![connection[1]]
                val pDist = sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))

                val cDist = referenceDistances[index]

                distances.add(calibrationDistance * (cDist / pDist))
            }

            var sum = 0.0
            var count = 0
            var min = distances[0]
            var max = distances[0]

            for(d in distances) {
                if(!d.isNaN()) {
                    sum += d
                    count++
                    if(d < min) min = d
                    if(d > max) max = d
                }
            }

            sum -= min + max

            return sum/(count-2)
        }
    }

    var measurer = defaultMeasurer
    lateinit var cbd: ArrayList<ArrayList<PointF>>

    fun startCalibration() {
        calibrated = false
        val calibrationData = ArrayList<ArrayList<PointF>>()
        cbd = calibrationData

        var calibrationArrayReady = false



        measurer = object : FaceMeter {
            override fun measure(face: Face): Double {
                if(!isPostureCorrect(face)) return -1.0
                var r = 0.0
                val points = FaceData(face)
                if(!points.isOk)
                    return -1.0

                if(!calibrationArrayReady) {
                    for(conn in 0..points.facePoints!!.size-1) {
                        calibrationData.add(ArrayList())
                    }

                    calibrationArrayReady = true
                }

                for((index, p) in points.facePoints!!.withIndex()) {
                    calibrationData[index].add(p)
                    if(index == 0)
                        r = calibrationData[index].size.toDouble() / numFacesToCalibration
                }

                if(r >= 1.0) {
                    calibrated = true
                    measurer = defaultMeasurer

                    for((distIndex, line) in faceConnections.withIndex()) {
                        var sumDist = 0.0
                        var count = 0

                        for(i in 0 until calibrationData[0].size) {
                            val p1 = calibrationData[line[0]][i]
                            val p2 = calibrationData[line[1]][i]
                            val d = sqrt((p1.x-p2.x).pow(2) + (p1.y-p2.y).pow(2))
                            if(!d.isNaN()) {
                                sumDist += d
                                count++
                            }
                        }
                        referenceDistances!![distIndex] = sumDist/count
                    }
                    Log.i(TAG, "nonni")
                }

                return r
            }

        }
    }
}

val DEX_RANGE = 0..3
val SIN_RANGE = 4..7

class FaceData(face: Face) {
    val isOk: Boolean
    var facePoints: Array<PointF>? = null

    init {
        val fPoints = ArrayList<PointF>()

        var contour = face.getContour(Contour.RIGHT_EYE)
        if(contour != null) {
            fPoints.add(contour.points[0])
            fPoints.add(contour.points[8])
        }

        contour = face.getContour(Contour.FACE)
        if(contour != null) {
            fPoints.add(contour.points[0])
            fPoints.add(contour.points[7])
            fPoints.add(contour.points[29])
            fPoints.add(contour.points[35])
        }

        contour = face.getContour(Contour.NOSE_BRIDGE)
        if(contour != null) {
            fPoints.add(contour.points[0])
        }

        contour = face.getContour(Contour.LEFT_EYE)
        if(contour != null) {
            fPoints.add(contour.points[0])
            fPoints.add(contour.points[8])
        }

        isOk = fPoints.size == 9

        if(isOk)
            facePoints = fPoints.toTypedArray()
    }
}

val faceConnections = arrayOf(
    // OIKEA
    arrayOf(1, 6), //silmä ulko -> nenän tyvi
    //arrayOf(6, 7),
    arrayOf(2, 6), // otsa -> nenän tyvi
    arrayOf(1, 2), // silmä ulko -> otsa
    arrayOf(0, 3), // silmä sisä -> korva yläreuna
    // VASEN
    arrayOf(7, 6),
    //arrayOf(6, 7),
    arrayOf(5, 6),
    arrayOf(7, 5),
    arrayOf(8, 4)
)
//const val numPoints = 9