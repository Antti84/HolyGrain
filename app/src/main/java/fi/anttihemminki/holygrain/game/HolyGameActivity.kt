package fi.anttihemminki.holygrain.game

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import fi.anttihemminki.holygrain.*
import fi.anttihemminki.holygrain.databinding.ActivityHolyGameBinding
import fi.anttihemminki.holygrain.facedistance.*

const val TESTI = false

enum class Direction {UP, RIGHT, DOWN, LEFT}

class HolyGameActivity : DistanceMeterActivity() {

    lateinit var binding: ActivityHolyGameBinding

    val visusManager = VisusManager()

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

    fun showBirds(show: Boolean) {
        val visibility = if(show) View.VISIBLE else View.INVISIBLE

        binding.gameBirdRight.visibility = visibility
        binding.gameBirdLeft.visibility = visibility
        binding.gameBirdTop.visibility = visibility
        binding.gameBirdBottom.visibility = visibility
    }

    fun enableBird(enable: Boolean, bird: Direction) {
        when(bird) {
            Direction.UP -> binding.gameBirdTop.isEnabled = enable
            Direction.RIGHT -> binding.gameBirdRight.isEnabled = enable
            Direction.DOWN -> binding.gameBirdBottom.isEnabled = enable
            Direction.LEFT -> binding.gameBirdLeft.isEnabled = enable
        }
    }

    fun enableBirds(enable: Boolean) {
        binding.gameBirdTop.isEnabled = enable
        binding.gameBirdRight.isEnabled = enable
        binding.gameBirdBottom.isEnabled = enable
        binding.gameBirdLeft.isEnabled = enable
    }

    fun toggleCalibrationBtn(show: Boolean = true, enable: Boolean = true) {
        binding.gameStartCalibrationBtn.isEnabled = enable
        binding.gameStartCalibrationBtn.visibility = if(show) View.VISIBLE else View.INVISIBLE
    }

    var boxColor = Color.rgb(0, 0, 0)
    var bgColor = Color.rgb(255, 255, 255)
    fun drawBox(gap: Int, direction: Direction): Bitmap {
        val boxSize = gap * 5
        val bitmap = Bitmap.createBitmap(boxSize, boxSize, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val paint = Paint()

        paint.color = boxColor
        canvas.drawRect(0F, 0F, boxSize.toFloat(), boxSize.toFloat(), paint)

        paint.color = bgColor
        canvas.drawRect(gap.toFloat(), gap.toFloat(), (gap*4).toFloat(), (gap*4).toFloat(), paint)

        when(direction) {
            Direction.UP -> canvas.drawRect((gap*2).toFloat(), 0F, (gap*3).toFloat(), gap.toFloat(), paint)
            Direction.RIGHT -> canvas.drawRect((gap*4).toFloat(), (gap*2).toFloat(), boxSize.toFloat(), (gap*3).toFloat(), paint)
            Direction.DOWN -> canvas.drawRect((gap*2).toFloat(), (gap*4).toFloat(), (gap*3).toFloat(), boxSize.toFloat(), paint)
            Direction.LEFT -> canvas.drawRect(0F, (gap*2).toFloat(), gap.toFloat(), (gap*3).toFloat(), paint)
        }

        return bitmap
    }

    fun showGameBoard() {
        binding.gameFaceImageView.visibility = View.INVISIBLE
        toggleCalibrationBtn(false, false)

        showBirds(true)

        val lento_right = binding.gameBirdRight.drawable as AnimationDrawable
        lento_right.start()

        val lento_left = binding.gameBirdLeft.drawable as AnimationDrawable
        lento_left.start()

        binding.boxImage.visibility = View.VISIBLE
        binding.boxImage.setImageBitmap(drawBox(10, Direction.DOWN))

        enableBirds(true)
    }

    fun notOneFaceVisibleCallback() {
        Log.e(TAG, "Yhtään naamaa ei näkyvissä, tai yli yksi naama näkyvissä!")
    }

    fun oneFaceVisibleCallback() {
        Log.i(TAG, "Hyvä, yksi naama näkyy.")
    }

    private fun getFaceDistance(distance: Double, time: Long) {
        //Log.i(TAG, "Distance: $distance, time: $time")
        visusManager.receiveDistance(distance, time)
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

    fun handleBirdClick(direction: Direction) {
        Log.i(TAG, "Bird $direction clicked")
    }

    fun rightBirdClick(view: View) { handleBirdClick(Direction.RIGHT) }
    fun bottomBirdClick(view: View) { handleBirdClick(Direction.DOWN) }
    fun topBirdClick(view: View) { handleBirdClick(Direction.UP) }
    fun leftBirdClick(view: View) { handleBirdClick(Direction.LEFT) }
}

enum class HolyGameState { NOT_CALIBRATED, CALIBRATING, CALIBRATED }