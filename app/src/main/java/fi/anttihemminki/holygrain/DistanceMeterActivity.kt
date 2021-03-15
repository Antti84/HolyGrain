package fi.anttihemminki.holygrain

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.ImageProxy
import com.google.android.gms.vision.face.Contour
import com.google.mlkit.vision.face.Face
import fi.anttihemminki.holygrain.databinding.ActivityDistanceMeterBinding
import fi.anttihemminki.holygrain.facedistance.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

enum class DistanceMeterActivityState { HEAD_POSTURE, CALIBRATING, CALIBRATED }

class DistanceMeterActivity : CameraActivity() {

    lateinit var binding: ActivityDistanceMeterBinding

    lateinit var faceDetector : HolyFaceDetector

    var drawFacePoints = true

    var distanceMeter = DistanceMeter()

    var state = DistanceMeterActivityState.HEAD_POSTURE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDistanceMeterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraView = binding.faceImageView

        faceDetector = HolyFaceDetector()

        distanceMeter.startCalibration()

        refreshUI()
    }

    fun refreshUI() {
        if(state == DistanceMeterActivityState.HEAD_POSTURE) {
            binding.guideTxt.text = "Asettele pää suoraan"
            binding.calibrateBtn.visibility = View.VISIBLE
            binding.calibrateBtn.isEnabled = false
            binding.calibrateBtn.setOnClickListener {this.startCalibration() }
        } else {
            binding.calibrateBtn.visibility = View.INVISIBLE
            binding.calibrateBtn.isEnabled = false
        }

        if(state == DistanceMeterActivityState.CALIBRATED) {
            binding.guideTxt.text = "Kalibrointi suoritettu"
        }
    }

    override fun receiveImage(imageProxy: ImageProxy, timeStamp: Long) {
        Log.i(HOLY_TAG, "ReceiveImage: $imageProxy, time: $timeStamp")

        faceDetector.analyze(imageProxy) { faces ->
            val data = RawFaceData(imageProxy, faces, timeStamp)
            receiveFaces(data)
        }
    }

    fun setBmpToView(bmp: Bitmap?) {
        if(bmp != null) {
            val bmp2 = bmp.flip(-1f, 1f, bmp.width/2f, bmp.height/2f)
            this.runOnUiThread {
                cameraView.setImageBitmap(bmp2)
            }
        }
    }

    fun receiveFaces(rawFaceData: RawFaceData) {
        var bmp = imageProxyToBitmap(rawFaceData.imageProxy)
        rawFaceData.imageProxy.close()

        if(rawFaceData.faces.size != 1) {
            setBmpToView(bmp)
            return
        }

        val face = rawFaceData.faces[0]

        val distance = distanceMeter.measurer.measure(face)
        if(distance == -1.0 && state != DistanceMeterActivityState.HEAD_POSTURE) {
            this.runOnUiThread {
                binding.guideTxt.text = "Pään asento ei oe kunnollinen. Tarkista että pää on suorassa ja keskellä kuvaa."
            }
            return
        }

        if(state == DistanceMeterActivityState.HEAD_POSTURE) {
            binding.calibrateBtn.isEnabled = true
        }
        if(state == DistanceMeterActivityState.CALIBRATING) {
            var progress = (distance * 100).toInt()
            if(progress > 100)
                progress = 100
            if(progress == 100) {
                state = DistanceMeterActivityState.CALIBRATED
                refreshUI()
            } else {
                this.runOnUiThread {
                    binding.guideTxt.text = "Calibration progress: ${progress}%"
                }
            }
        }

        if(state == DistanceMeterActivityState.CALIBRATED) {
            this.runOnUiThread {
                binding.guideTxt.text = "Current distance: ${distance}cm"
            }
        }

        if(drawFacePoints) {
            val facePoints = FaceData(face).facePoints
            bmp = drawFaceLinesToBitmap(bmp!!, facePoints!!, faceConnections)
        }

        setBmpToView(bmp)
    }

    fun getDistances(points: ArrayList<PointF>, lines: Array<Array<Int>>): ArrayList<Double> {
        val d = ArrayList<Double>()
        for(line in lines) {
            val p1 = points[line[0]]
            val p2 = points[line[1]]
            d.add(sqrt((p1.x-p2.x).toDouble().pow(2) + (p1.y-p2.y).toDouble().pow(2)))
        }
        return d
    }

    fun startCalibration() {
        this.state = DistanceMeterActivityState.CALIBRATING
        distanceMeter.startCalibration()
        refreshUI()
    }

    fun btnClick(view: View) {}

    /*fun enableShowView(view: View, enable: Boolean = true) {
        view.isEnabled = enable
        view.visibility = if (enable) View.VISIBLE else View.INVISIBLE
    }*/

    /*var faceIdTarjolla = -1
    override fun receiveFaceData(faces: MutableList<Face>, time: Long) {
        super.receiveFaceData(faces, time)

        binding.faceNumTxt.text = "Faces visible: ${faces.size}"

        if(state == State.TRACKING_ID) {
            if (faces.size == 1) {
                enableShowView(binding.selectFaceBtn, true)
                if (faceId == -1 && faces[0].trackingId != null) {
                    faceIdTarjolla = faces[0].trackingId!!
                    binding.chosenFaceId.text = "faceIdTarjolla: $faceIdTarjolla"
                }
            } else {
                enableShowView(binding.selectFaceBtn, false)
            }
        } else if(state == State.CALIBRATING) {

        } else if(state == State.SHOW_CURRENT_DISTANCE) {
            var min = 1000000.0
            var max = -1.0
            var mean = 0.0


        }
    }

    fun selectFace(view: View) {
        if(state == State.TRACKING_ID) {
            state = State.CALIBRATION_NOT_STARTED

            faceId = faceIdTarjolla
            enableShowView(binding.selectFaceBtn, false)
            binding.chosenFaceId.text = "Valittu naama: $faceId"

            enableShowView(binding.calibrateBtn, true)
        }
    }

    fun startCalibration(view: View) {
        if(state == State.CALIBRATION_NOT_STARTED) {

            distanceMeter.startCalibration(faceId, {
                binding.calibrationTxt.text = "Kalibroitu: ${distanceMeter.calibrationFaces.size}/${distanceMeter.numFacesToCalibration}"
                state = State.SHOW_CURRENT_DISTANCE
            },
                {
                binding.calibrationTxt.text = "Kalibrointifactor: ${distanceMeter.calibrationFactors.toString()}"
            }, object: DistanceAndImageReceiverInterface {
                override fun receiveDistanceAndImage(image: ImageProxy, distances: ArrayList<Double>, time: Long) {
                    sendDistanceData("TestName", "TestSet", 0, distances, time,
                            {
                                Log.i(HOLY_TAG, "ok")
                            },
                            {
                                Log.i(HOLY_TAG, "not ok")
                            })
                    try {
                        setImageToLayout(image)
                    } catch (error: Exception) {}
                    runOnUiThread {
                        binding.calibrationTxt.text = distances.toString()
                    }
                }

            })

            state = State.CALIBRATING
        }
    }

    enum class State { TRACKING_ID, CALIBRATION_NOT_STARTED, CALIBRATING, SHOW_CURRENT_DISTANCE }
    var state = State.TRACKING_ID*/

}