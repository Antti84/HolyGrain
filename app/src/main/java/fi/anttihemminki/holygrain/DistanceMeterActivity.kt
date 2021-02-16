package fi.anttihemminki.holygrain

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.face.Face
import fi.anttihemminki.holygrain.databinding.ActivityDistanceMeterBinding
import fi.anttihemminki.holygrain.facedistance.DistanceAndImageReceiverInterface

class DistanceMeterActivity : CameraActivity() {

    lateinit var binding: ActivityDistanceMeterBinding
    //var faceId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDistanceMeterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraView = binding.faceImageView

        binding.faceNumTxt.text = "testi"
    }

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