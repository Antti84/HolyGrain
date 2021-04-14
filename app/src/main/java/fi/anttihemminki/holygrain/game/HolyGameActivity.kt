package fi.anttihemminki.holygrain.game

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import fi.anttihemminki.holygrain.*
import fi.anttihemminki.holygrain.databinding.ActivityHolyGameBinding
import fi.anttihemminki.holygrain.facedistance.*

const val TESTI = true

class HolyGameActivity : DistanceMeterActivity() {

    lateinit var binding: ActivityHolyGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHolyGameBinding.inflate(layoutInflater)

        setContentView(binding.root)

        cameraView = binding.gameFaceImageView

        toggleCalibrationBtn()

        if(TESTI) {
            state = HolyGameState.CALIBRATED
            showGameBoard()
        }
    }

    fun toggleCalibrationBtn(show: Boolean = true, enable: Boolean = true) {
        binding.gameStartCalibrationBtn.isEnabled = enable
        binding.gameStartCalibrationBtn.visibility = if(show) View.VISIBLE else View.INVISIBLE
    }

    fun showGameBoard() {
        binding.gameFaceImageView.visibility = View.INVISIBLE
        toggleCalibrationBtn(false, false)
        binding.gameBirdRight.visibility = View.VISIBLE
        binding.gameBirdLeft.visibility = View.VISIBLE

        val lento_right = binding.gameBirdRight.drawable as AnimationDrawable
        lento_right.start()

        val lento_left = binding.gameBirdLeft.drawable as AnimationDrawable
        lento_left.start()
    }

    fun notOneFaceVisibleCallback() {
        Log.e(TAG, "Yhtään naamaa ei näkyvissä, tai yli yksi naama näkyvissä!")
    }

    fun oneFaceVisibleCallback() {
        Log.i(TAG, "Hyvä, yksi naama näkyy.")
    }

    fun getFaceDistance(distance: Double, time: Long) {
        Log.i(TAG, "Distance: $distance, time: $time")
    }

    override fun receiveFaces(rawFaceData: RawFaceData) {
        var bmp = imageProxyToBitmap(rawFaceData.imageProxy)
        rawFaceData.imageProxy.close()

        if(state == HolyGameState.NOT_CALIBRATED) {
            setBmpToView(bmp)
            return
        }

        if(rawFaceData.faces.size != 1) {
            setBmpToView(bmp)
            runOnUiThread { notOneFaceVisibleCallback() }
            return
        }

        val eyes = Eyes.OA
        val face = rawFaceData.faces[0]

        val distance = distanceMeter.measurer.measure(face, eyes)

        if(state == HolyGameState.CALIBRATING) {
            var progress = (distance * 100).toInt()
            if(progress > 100)
                progress = 100
            if(progress == 100) {
                Log.i(TAG, "Calibrated!")
                state = HolyGameState.CALIBRATED
                showGameBoard()
            } else {
                Log.i(TAG, "Calibrating prgress: ${progress}%")
                //this.runOnUiThread {
                    //binding.guideTxt.text = "Calibration progress: ${progress}%"
                //}
            }
        }

        runOnUiThread { oneFaceVisibleCallback() }

        if(distance == -1.0) {
            /*this.runOnUiThread {
                binding.guideTxt.text = "Pään asento ei oe kunnollinen. Tarkista että pää on suorassa ja keskellä kuvaa."
            }*/
            return
        }

        getFaceDistance(distance, rawFaceData.timeStamp)

        /*if(drawFacePoints) {
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
        }*/

        setBmpToView(bmp)
    }

    var state = HolyGameState.NOT_CALIBRATED
    fun gameStartCalibration(view: View) {
        toggleCalibrationBtn(show = false, enable = false)
        state = HolyGameState.CALIBRATING
        distanceMeter.startCalibration()
    }
}

enum class HolyGameState { NOT_CALIBRATED, CALIBRATING, CALIBRATED }