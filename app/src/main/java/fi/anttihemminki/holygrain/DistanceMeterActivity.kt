package fi.anttihemminki.holygrain

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.ImageProxy
import fi.anttihemminki.holygrain.databinding.ActivityDistanceMeterBinding
import fi.anttihemminki.holygrain.facedistance.*
import kotlin.math.pow
import kotlin.math.sqrt

enum class DistanceMeterActivityState { HEAD_POSTURE, CALIBRATING, CALIBRATED, COLLECTING }

data class DistanceData(
        val ID: Int,
        val testState: TestSetStateData,
        val index: Int,
        val time: Long,
        val distance: Double,
        val rotx: Float, val roty: Float, val rotz: Float,
        val leftEyeOpenProb: Float, val rightEyeOpenProb: Float
)

open class DistanceMeterActivity : CameraActivity() {

    private lateinit var binding: ActivityDistanceMeterBinding

    private lateinit var faceDetector : HolyFaceDetector

    private var drawFacePoints = true

    protected var distanceMeter = DistanceMeter()

    private var state = DistanceMeterActivityState.HEAD_POSTURE

    private var dId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDistanceMeterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(this.intent != null && this.intent.extras != null) {
            dId = this.intent.extras!!.getInt("DistanceStudyId", -1)
        }
        if(dId !in IDMIN..IDMAX){
            goBackToMainMenu()
        }

        cameraView = binding.faceImageView

        faceDetector = HolyFaceDetector()

        refreshUI()
    }

    private fun goBackToMainMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun refreshUI() {
        if(state == DistanceMeterActivityState.HEAD_POSTURE) {
            binding.guideTxt.text = "Asettele pää suoraan"

            with(binding.calibrateBtn) {
                visibility = View.VISIBLE
                isEnabled = false
                setOnClickListener { startCalibration() }
            }

        } else {
            //binding.calibrateBtn.visibility = View.INVISIBLE
            binding.calibrateBtn.isEnabled = false
        }

        if(state == DistanceMeterActivityState.CALIBRATED) {
            binding.guideTxt.text = "Kalibrointi suoritettu"

            with(binding.calibrateBtn) {
                text = "Kerää dataa"
                visibility = View.VISIBLE
                isEnabled = true
                setOnClickListener { continueDataCollection() }
            }

            dataStateClass = TestSetState()
            state = DistanceMeterActivityState.COLLECTING
            dataState = dataStateClass.getCurrentState()
            binding.guideTxt.text = testSetStateDataToPositionString(dataState)
        }
    }

    lateinit var dataStateClass: TestSetState
    lateinit var dataState: TestSetStateData
    var numMeasures: Int = 0
    val measuresPerSet = 25
    var collecting = false
    fun continueDataCollection() {
        binding.calibrateBtn.isEnabled = false
        dataState = dataStateClass.getCurrentState()
        numMeasures = 0
        runOnUiThread { binding.numTxt.text = "${numMeasures}/${measuresPerSet}" }
        collecting = true
    }

    override fun receiveImage(imageProxy: ImageProxy, timeStamp: Long) {
        //Log.i(HOLY_TAG, "ReceiveImage: $imageProxy, time: $timeStamp")

        faceDetector.analyze(imageProxy) { faces ->
            val data = RawFaceData(imageProxy, faces, timeStamp)
            receiveFaces(data)
        }
    }

    fun setBmpToView(bmp: Bitmap?) {
        if (cameraView != null && bmp != null) {
            val bmp2 = bmp.flip(-1f, 1f, bmp.width / 2f, bmp.height / 2f)
            this.runOnUiThread {
                cameraView!!.setImageBitmap(bmp2)
            }
        }
    }

    open fun receiveFaces(rawFaceData: RawFaceData) {
        var bmp = imageProxyToBitmap(rawFaceData.imageProxy)
        rawFaceData.imageProxy.close()

        if(rawFaceData.faces.size != 1) {
            setBmpToView(bmp)
            runOnUiThread { binding.hintTxt.text = "Naamaa ei näy" }
            return
        }

        runOnUiThread { binding.hintTxt.text = "Naama ok" }

        val face = rawFaceData.faces[0]

        var eyes = Eyes.OA
        if(state == DistanceMeterActivityState.COLLECTING && dataState.eye != Eyes.OA) {
            try {
                eyes = dataState.eye
            } catch (error: UninitializedPropertyAccessException) {
            }
        }

        val distance = distanceMeter.measurer.measure(face, eyes)
        if(distance == -1.0 && state != DistanceMeterActivityState.HEAD_POSTURE) {
            /*this.runOnUiThread {
                binding.guideTxt.text = "Pään asento ei oe kunnollinen. Tarkista että pää on suorassa ja keskellä kuvaa."
            }*/
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

        if(state == DistanceMeterActivityState.COLLECTING && collecting) {
            //Log.i("DataCollection", "$distance $numMeasures")
            numMeasures += 1

            runOnUiThread { binding.numTxt.text = "${numMeasures}/${measuresPerSet}" }

            val d = DistanceData(
                    dId, dataState,
                    numMeasures,
                    rawFaceData.timeStamp, distance,
                    rawFaceData.faces[0].headEulerAngleX,
                    rawFaceData.faces[0].headEulerAngleY,
                    rawFaceData.faces[0].headEulerAngleZ,
                    rawFaceData.faces[0].leftEyeOpenProbability!!,
                    rawFaceData.faces[0].rightEyeOpenProbability!!,
                    )
            sendDistanceData(d, {Log.i("HOLY_SERVER", "Distance send ok")},
                    {Log.e("HOLY_SERVER", "Distance send error")})

            if(numMeasures >= measuresPerSet) {
                collecting = false
                val canContinue = dataStateClass.goToNextState()
                if(!canContinue) {
                    goBackToMainMenu()
                } else {
                    binding.calibrateBtn.isEnabled = true
                    dataState = dataStateClass.getCurrentState()
                    binding.guideTxt.text = testSetStateDataToPositionString(dataState)
                }
            }
        }

        if(drawFacePoints) {
            var connectionsToTest = faceConnections
            if(state == DistanceMeterActivityState.COLLECTING && dataState.eye != Eyes.OA) {
                try {
                    if (dataState.eye == Eyes.OD) {
                        connectionsToTest = faceConnections.slice(DEX_RANGE).toTypedArray()
                    } else if (dataState.eye == Eyes.OS) {
                        connectionsToTest = faceConnections.slice(SIN_RANGE).toTypedArray()
                    }
                } catch (error: UninitializedPropertyAccessException) {
                }
            }

            val facePoints = FaceData(face).facePoints
            bmp = drawFaceLinesToBitmap(bmp!!, facePoints!!, connectionsToTest)
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
}