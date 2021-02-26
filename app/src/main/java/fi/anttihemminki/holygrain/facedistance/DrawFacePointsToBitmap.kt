package fi.anttihemminki.holygrain.facedistance

import android.graphics.*
import com.google.mlkit.vision.face.Face
import fi.anttihemminki.holygrain.FaceData
import fi.anttihemminki.holygrain.FacePoint
import fi.anttihemminki.holygrain.FacePointType
import fi.anttihemminki.holygrain.holycamera.contours
import fi.anttihemminki.holygrain.holycamera.landmarks
import java.lang.IllegalArgumentException

fun drawFacePointsToBitmap(image: Bitmap, facePoints: ArrayList<PointF>): Bitmap {
    /*try {
        requireNotNull(face.trackingId)
    } catch (error: IllegalArgumentException) {
        return image
    }*/

    val bitmapConfig = image.config

    val bitmap = image.copy(bitmapConfig, true)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    paint.color = Color.GREEN
    paint.strokeWidth = 5F
    paint.style = Paint.Style.STROKE

    //val facePoints = getFacePoints(face)

    //canvas.drawRect(face.boundingBox, paint)

    for(p in facePoints) {
        canvas.drawPoint(p.x, p.y, paint)
    }

    return bitmap
}

fun drawFaceLinesToBitmap(image: Bitmap, facePoints: ArrayList<PointF>,
                          lines: Array<Array<Int>>):  Bitmap {
    val bitmapConfig = image.config

    val bitmap = image.copy(bitmapConfig, true)
    val canvas = Canvas(bitmap)
    val paint = Paint()

    paint.color = Color.GREEN
    paint.strokeWidth = 2F
    paint.style = Paint.Style.STROKE

    for(line in lines) {
        if(line.size == 2) {
            if(facePoints.size > line[0] && facePoints.size > line[1]) {
                canvas.drawLine(
                    facePoints[line[0]].x,
                    facePoints[line[0]].y,
                    facePoints[line[1]].x,
                    facePoints[line[1]].y,
                    paint
                )
            }
        }
        //canvas.drawPoint(p.x, p.y, paint)
    }

    return bitmap
}
/*
fun getFacePoints(face: Face): ArrayList<FacePoint> {
    val facePoints = ArrayList<FacePoint>()

    for(landmark in landmarks) {
        val lm = face.getLandmark(landmark)
        lm?.let {
            val fp = FacePoint(lm.position, landmark, FacePointType.LANDMARK, -1)
            facePoints.add(fp)
        }
    }

    for(contour in contours) {
        val contData = face.getContour(contour)?.points
        if (contData != null) {
            for((index, point) in contData.withIndex()) {
                val fp = FacePoint(point, contour, FacePointType.CONTOUR, index)
                facePoints.add(fp)

            }
        }
    }

    return facePoints
}*/