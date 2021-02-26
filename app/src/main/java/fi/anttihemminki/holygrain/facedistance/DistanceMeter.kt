package fi.anttihemminki.holygrain.facedistance

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import com.google.android.gms.vision.face.Contour
import com.google.mlkit.vision.face.*
import fi.anttihemminki.holygrain.holycamera.landmarks
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

const val TAG = "DistanceMeter"

interface FaceMeter {
    fun measure(face: Face): Double
}

class DistanceMeter() {
    val numFacesToCalibration = 30

    val referenceLines = ArrayList<PointF>()

    val defaultMeasurer = object : FaceMeter {
        override fun measure(face: Face): Double {
            return 1.01
        }
    }

    var measurer = defaultMeasurer

    fun startCalibration() {
        val calibrationData = ArrayList<ArrayList<PointF>>()

        for(conn in 0..(numPoints+1)) {
            calibrationData.add(ArrayList())
        }

        measurer = object : FaceMeter {
            override fun measure(face: Face): Double {
                var r = 0.0
                val points = getFacePoints(face)
                for((index, p) in points.withIndex()) {
                    calibrationData[index].add(p)
                    if(index == 0)
                        r = calibrationData[index].size.toDouble() / numFacesToCalibration
                }

                calibrationData[calibrationData.size-1].add(PointF(
                    face.headEulerAngleX,
                    face.headEulerAngleY
                ))

                if(r >= 1.0) {
                    measurer = defaultMeasurer
                    referenceLines.clear()

                    for(line in faceConnections) {
                        var sumVec = PointF(0.0f, 0.0f)
                        for(i in 0..(calibrationData[0].size-1)) {
                            val p1 = calibrationData[line[0]][i]
                            val p2 = calibrationData[line[1]][i]
                            sumVec.x += abs(p1.x-p2.x)
                            sumVec.y += abs(p1.y-p2.y)
                        }
                        sumVec.x /= calibrationData[0].size
                        sumVec.y /= calibrationData[0].size
                        referenceLines.add(sumVec)
                    }
                }

                return r
            }

        }
    }


}

val DEX_RANGE = 0..3
val SIN_RANGE = 4..7
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

val numPoints = 9
fun getFacePoints(face: Face): ArrayList<PointF> {
    val facePoints = ArrayList<PointF>()

    var contour = face.getContour(Contour.RIGHT_EYE)
    if(contour != null) {
        facePoints.add(contour.points[0])
        facePoints.add(contour.points[8])
        //binding.chosenFaceId.text = contour.points.size.toString()
    }

    contour = face.getContour(Contour.FACE)
    if(contour != null) {
        facePoints.add(contour.points[0])
        facePoints.add(contour.points[7])
        //facePoints.add(contour.points[11])
        //facePoints.add(contour.points[25])
        facePoints.add(contour.points[29])
        facePoints.add(contour.points[35])
        //binding.chosenFaceId.text = contour.points.size.toString()
    }

    contour = face.getContour(Contour.NOSE_BRIDGE)
    if(contour != null) {
        facePoints.add(contour.points[0])
    }

    contour = face.getContour(Contour.LEFT_EYE)
    if(contour != null) {
        facePoints.add(contour.points[0])
        facePoints.add(contour.points[8])
        //facePoints.add(contour.points[10])
    }

    return facePoints
}