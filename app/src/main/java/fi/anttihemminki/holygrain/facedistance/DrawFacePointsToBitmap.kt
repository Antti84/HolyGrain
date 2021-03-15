package fi.anttihemminki.holygrain.facedistance

import android.graphics.*

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

fun drawFaceLinesToBitmap(image: Bitmap, facePoints: Array<PointF>,
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