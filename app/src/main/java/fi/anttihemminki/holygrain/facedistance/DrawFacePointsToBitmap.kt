package fi.anttihemminki.holygrain.facedistance

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.face.Face
import fi.anttihemminki.holygrain.FaceData
import fi.anttihemminki.holygrain.FacePoint
import fi.anttihemminki.holygrain.FacePointType
import fi.anttihemminki.holygrain.holycamera.contours
import fi.anttihemminki.holygrain.holycamera.landmarks
import java.lang.IllegalArgumentException

fun drawFacePointsToBitmap(image: Bitmap, face: Face): Bitmap {
    try {
        requireNotNull(face.trackingId)
    } catch (error: IllegalArgumentException) {
        return image
    }

    val bitmapConfig = image.config

    val bitmap = image.copy(bitmapConfig, true)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    paint.color = Color.RED
    paint.strokeWidth = 2F
    paint.style = Paint.Style.STROKE

    val facePoints = ArrayList<FacePoint>()

    canvas.drawRect(face.boundingBox, paint)

    for(landmark in landmarks) {
        val lm = face.getLandmark(landmark)
        lm?.let {
            canvas.drawPoint(lm.position.x, lm.position.y, paint)
            val fp = FacePoint(lm.position, landmark, FacePointType.LANDMARK, -1)
            facePoints.add(fp)
        }
    }

    for(contour in contours) {
        val contData = face.getContour(contour)?.points
        if (contData != null) {
            for((index, point) in contData.withIndex()) {
                canvas.drawPoint(point.x, point.y, paint)
                val fp = FacePoint(point, contour, FacePointType.CONTOUR, index)
                facePoints.add(fp)

            }
        }
    }

    val faceData = FaceData(
            "Antti",
            "TESTI",//testSet.name,
            0,//time,
            face.boundingBox,
            face.trackingId!!,
            face.headEulerAngleX,
            face.headEulerAngleY,
            face.headEulerAngleZ,
            face.smilingProbability!!,
            face.rightEyeOpenProbability!!,
            face.leftEyeOpenProbability!!,
            facePoints
    )

    return bitmap
}