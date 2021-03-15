package fi.anttihemminki.holygrain.distancestudy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.mlkit.vision.face.Face
import fi.anttihemminki.holygrain.*
import fi.anttihemminki.holygrain.databinding.DistanceActivityBinding
import org.json.JSONObject
import java.lang.Exception

const val MINIMUM_TEST_BLOCK_NAME_LENGTH = 5

abstract class DistanceStudyActivity : CameraActivity() {

    var testSetState = TestSetState.EI_ALOITETTU

    //val testData = ArrayList<FaceData>()

    lateinit var binding: DistanceActivityBinding

    var faceName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DistanceActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activateControls(false)

        testTextView = binding.testTextView
        cameraView = binding.cameraImageView

        testServer(
            { binding.testPersonNameEditText.isEnabled = true },
            { binding.startBtn.text = "Serveri ei jostain syystä toimi." }
        )

        binding.testCreationHintText.text = "Anna testiblokin nimi ensin."

        binding.testPersonNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length >= MINIMUM_TEST_BLOCK_NAME_LENGTH) {
                    tryValidateTestBlockName()
                }
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        //drawDotsToImage = true
    }

    fun activateControls(activate: Boolean) {
        binding.startBtn.isEnabled = activate
        binding.testPersonNameEditText.isEnabled = activate
    }

    fun tryValidateTestBlockName() {
        val t = binding.testPersonNameEditText.text.toString()
        if(t.length >= MINIMUM_TEST_BLOCK_NAME_LENGTH) {
            validateTestsetName(t, { data ->
                try {
                    val v = data.getString("testset_name")
                    if(v == "ok") {
                        binding.startBtn.text = "Luo testi: $t"
                        binding.startBtn.isEnabled = true
                    }
                } catch (error: Exception) {}
            }) {

            }
        }
    }

    fun testsetCreationSuccesHandler(json: JSONObject) {
        when(json.getString("testset_name")) {
            "in_use" -> binding.testCreationHintText.text = getString(R.string.testset_name_in_use)
            "creation error" -> binding.testCreationHintText.text = getString(R.string.testset_name_creation_error)
            "ok" ->  {
                faceName = binding.testPersonNameEditText.text.toString()
                binding.startBtn.text = getString(R.string.ready)
                binding.testCreationHintText.text = getString(R.string.aseta_naama_txt)
                binding.testPersonNameEditText.visibility = View.INVISIBLE
                testSetState = TestSetState.ASETTELE_KASVO
            }
        }
    }

    fun startBtnPressed(view: View) {
        if(testSetState == TestSetState.EI_ALOITETTU) {
            binding.testPersonNameEditText.isEnabled = false
            val t = binding.testPersonNameEditText.text.toString()
            createTestset(t, { testsetCreationSuccesHandler(it) }, {})
        } else if(testSetState == TestSetState.ASETTELE_KASVO) {
            testSetState = TestSetState.VALITE_KASVO
        } else if(testSetState > TestSetState.VALITE_KASVO) {
            collectingData = true
            pendingToStartPhase = false
            binding.startBtn.isEnabled = false
            binding.startBtn.visibility = View.INVISIBLE
        }
    }

    var collectingData = false
    var numCollected = 0
    var trackingIdTarjolla = -1
    var pendingToStartPhase = false

    fun receiveFaceData(faces: MutableList<Face>, time: Long) {
        //super.receiveFaceData(faces, time)
        if(testSetState == TestSetState.ASETTELE_KASVO || testSetState == TestSetState.EI_ALOITETTU)
            return

        if(testSetState == TestSetState.VALITE_KASVO) {
            if(trackingIdTarjolla == -1) {
                //super.receiveFaceData(faces, time)
                if(faces.size == 1) {
                    freezeImage = true
                    trackingIdTarjolla = faces[0].trackingId!!
                    binding.testCreationHintText.text = "Onko näkyvä & valittu naama (id $trackingIdTarjolla) oikea?"

                    binding.startBtn.visibility = View.INVISIBLE
                    binding.startBtn.isEnabled = false

                    binding.yesBtn.visibility = View.VISIBLE
                    binding.yesBtn.isEnabled = true

                    binding.noBtn.visibility = View.VISIBLE
                    binding.noBtn.isEnabled = true
                }
            }

            return
        }


        if(faces.size == 0) {
            return
        }
        var face: Face? = null

        for(f in faces) {
            /*if(f.trackingId == trackingFaceId) {
                face = f
                break
            }*/
        }

        if(face == null) {
            return
        }

        if(!pendingToStartPhase && collectingData) {
            if (testSetState != TestSetState.EI_ALOITETTU) {
                if(numCollected < testSetState.numMeasures) {
                    /*val fd = FaceData(
                            faceName,
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
                    this.testData.add(fd)
                    saveTestset(faceName, testSetState.toString(), numCollected, fd,
                            {
                                Log.i(HOLY_TAG, it.toString())
                            },
                            {
                                Log.e(HOLY_TAG, "error")
                            })
                    numCollected++*/

                    binding.testTextView.text = "Kerätty: ${numCollected}/${testSetState.numMeasures}"
                } else {
                    pendingToStartPhase = true
                    binding.startBtn.text = "Paina kun valmista"
                    binding.startBtn.isEnabled = true
                    binding.startBtn.visibility = View.VISIBLE
                    Log.i("DistanceStudyActivity", "TÄYNNÄ")
                    toNextState()
                }
            }
        }
    }

    fun toNextState() {
        val nextState = testSetState.getNext()
        if(nextState != null) {
            testSetState = nextState
            binding.testCreationHintText.text = testSetState.hint
            numCollected = 0
            pendingToStartPhase = true
        } else {
            // VALMIS
        }
    }

    fun yesBtnPressed(view: View) {
        if(testSetState == TestSetState.VALITE_KASVO) {
            //trackingFaceId = trackingIdTarjolla
            freezeImage = false

            binding.noBtn.visibility = View.INVISIBLE
            binding.yesBtn.visibility = View.INVISIBLE

            binding.noBtn.isEnabled = false
            binding.yesBtn.isEnabled = false

            binding.startBtn.visibility = View.VISIBLE
            binding.startBtn.isEnabled = true
            binding.startBtn.text = "Paina kun valmista"

            toNextState()
        }
    }

    fun noBtnPressed(view: View) {
        if(testSetState == TestSetState.VALITE_KASVO) {
            trackingIdTarjolla = -1
            freezeImage = false
        }
    }

    fun testButtonPressed(view: View) {
        freezeImage = !freezeImage
    }
}