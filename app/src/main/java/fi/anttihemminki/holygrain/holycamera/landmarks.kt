package fi.anttihemminki.holygrain.holycamera

import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark

val landmarks = arrayOf(
        FaceLandmark.LEFT_EAR,
        FaceLandmark.RIGHT_EAR,
        FaceLandmark.LEFT_CHEEK,
        FaceLandmark.LEFT_EYE,
        FaceLandmark.MOUTH_BOTTOM,
        FaceLandmark.MOUTH_LEFT,
        FaceLandmark.MOUTH_RIGHT,
        FaceLandmark.NOSE_BASE,
        FaceLandmark.RIGHT_CHEEK,
        FaceLandmark.RIGHT_EYE,
)

val contours = arrayOf(
        FaceContour.LEFT_EYE,
        FaceContour.FACE,
        FaceContour.LEFT_CHEEK,
        FaceContour.LEFT_EYEBROW_BOTTOM,
        FaceContour.LEFT_EYEBROW_TOP,
        FaceContour.LOWER_LIP_BOTTOM,
        FaceContour.LOWER_LIP_TOP,
        FaceContour.NOSE_BOTTOM,
        FaceContour.NOSE_BRIDGE,
        FaceContour.RIGHT_CHEEK,
        FaceContour.RIGHT_EYE,
        FaceContour.RIGHT_EYEBROW_BOTTOM,
        FaceContour.RIGHT_EYEBROW_TOP,
        FaceContour.UPPER_LIP_BOTTOM,
        FaceContour.UPPER_LIP_TOP,
)