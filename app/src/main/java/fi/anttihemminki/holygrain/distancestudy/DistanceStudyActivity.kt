package fi.anttihemminki.holygrain.distancestudy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.mlkit.vision.face.Face
import fi.anttihemminki.holygrain.*
import fi.anttihemminki.holygrain.databinding.DistanceActivityBinding
import fi.anttihemminki.holygrain.facedistance.getFacePoints

const val MINIMUM_TEST_BLOCK_NAME_LENGTH = 5

class DistanceStudyActivity : CameraActivity() {

    var testSetState = TestSetState.EI_ALOITETTU

    val testData = ArrayList<FaceData>()

    lateinit var distanceBinding: DistanceActivityBinding

    var testPersonTrackingId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        distanceBinding = DistanceActivityBinding.inflate(layoutInflater)
        setContentView(distanceBinding.root)

        testTextView = distanceBinding.testTextView
        cameraView = distanceBinding.cameraImageView

        testServer(
            { distanceBinding.startBtn.text = "Serveri toimii." },
            { distanceBinding.startBtn.text = "Serveri ei jostain syystä toimi." }
        )

        //binding.testCreationHintText.text = "Anna testiblokin nimi ensin."

        distanceBinding.testPersonNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length >= MINIMUM_TEST_BLOCK_NAME_LENGTH) {
                    tryValidateTestBlockName()
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        drawDotsToImage = true
    }

    fun tryValidateTestBlockName() {
        /*if(binding.testPersonNameEditText.text.length >= MINIMUM_TEST_BLOCK_NAME_LENGTH) {
            binding.startBtn.text = "NAPPI"
            binding.startBtn.visibility = VISIBLE
        } else {
            binding.startBtn.text = ""
            binding.startBtn.visibility = INVISIBLE
        }*/
    }


    fun startBtnPressed(view: View) {
        if(testSetState == TestSetState.EI_ALOITETTU) {
            testSetState = TestSetState.VALITE_KASVO
            //collectingData = true
            //numCollected = 0
        }
    }

    var collectingData = false
    var numCollected = 0
    var trackingIdTarjolla = -1
    override fun receiveFaceData(faces: MutableList<Face>, time: Long) {
        super.receiveFaceData(faces, time)
        if(testSetState == TestSetState.VALITE_KASVO) {
            if(trackingIdTarjolla == -1) {
                //super.receiveFaceData(faces, time)
                if(faces.size == 1) {
                    freezeFaceImage = true
                    trackingIdTarjolla = faces[0].trackingId
                    distanceBinding.testCreationHintText.text = "Onko näkyvä & valittu naama (id $trackingIdTarjolla) oikea?"
                }
            }

            return
        }


        if(faces.size == 0) {
            return
        }
        val face = faces[0]

        if(collectingData) {
            if (testSetState != TestSetState.EI_ALOITETTU) {
                if(numCollected < testSetState.numMeasures) {
                    this.testData.add(
                            FaceData(
                                    "TESTI",
                                    testSetState.toString(),
                                    time,
                                    face.boundingBox,
                                    face.trackingId!!,
                                    face.headEulerAngleX,
                                    face.headEulerAngleY,
                                    face.headEulerAngleZ,
                                    face.smilingProbability!!,
                                    face.rightEyeOpenProbability!!,
                                    face.leftEyeOpenProbability!!,
                                    getFacePoints(face)
                            )
                    )
                    numCollected++
                } else {
                    Log.i("DistanceStudyActivity", "TÄYNNÄ")
                }
            }
        }
    }

    fun shitchDots(view: View) {
        drawDotsToImage = !drawDotsToImage
        var s = "Pisteet: "
        when(drawDotsToImage) {
            true -> s += "POIS"
            false -> s += "PÄÄLLÄ"
        }
        distanceBinding.switchDotsBtn.text = s
    }
}