package fi.anttihemminki.holygrain

import android.graphics.PointF
import android.graphics.Rect

data class FaceData(
    val faceName: String,
    val setName: String,
    val time: Long,
    val boundingBox: Rect,
    val trackingId: Int,
    val rotX: Float,
    val rotY: Float,
    val rotZ: Float,
    val smilingProbability: Float,
    val rightEyeOpenProbability: Float,
    val leftEyeOpenProbability: Float,
    val facePoints: ArrayList<FacePoint>
)

enum class FacePointType() {
    LANDMARK,
    CONTOUR,
}

data class FacePoint(val coords: PointF, val typeIndex: Int, val type: FacePointType, val index: Int)