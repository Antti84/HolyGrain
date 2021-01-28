package fi.anttihemminki.holygrain.distancestudy

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.face.Face
import fi.anttihemminki.holygrain.CameraActivity
import fi.anttihemminki.holygrain.TestSet
import fi.anttihemminki.holygrain.databinding.DistanceActivityBinding
import fi.anttihemminki.holygrain.facedistance.drawFacePointsToBitmap
import fi.anttihemminki.holygrain.testServer

const val MINIMUM_TEST_BLOCK_NAME_LENGTH = 5

class DistanceStudyActivity() : CameraActivity() {

    enum class State {
        SHOW_PREVIEW, SHOW_DOTS, SHOW_NOTHING
    }

    var cameraImageState = State.SHOW_PREVIEW

    lateinit var binding: DistanceActivityBinding
    lateinit var previewView: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DistanceActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.cameraImageView.visibility = INVISIBLE

        /*testServer(
            { binding.serverTextView.text = "Serveri toimii." },
            { binding.serverTextView.text = "Erveri ei jostain syystä toimi." }
        )*/

        //binding.startBtn.text = ""
        //binding.startBtn.visibility = INVISIBLE

        binding.testCreationHintText.text = "Anna testiblokin nimi ensin."

        binding.testPersonNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!!.length >= MINIMUM_TEST_BLOCK_NAME_LENGTH) {
                    tryValidateTestBlockName()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //TODO("Not yet implemented")
            }

        })
    }

    fun receiveFaceData(image: Bitmap, faces: MutableList<Face>, time: Long) {

        /*if(drawFacePoints) {
            var img: Bitmap = image
            for(face in faces) {
                img = drawFacePointsToBitmap(img, face)
            }
            dotsImageView?.setImageBitmap(img)
        } else {
            if(dotsImageView != null) {
                dotsImageView!!.setImageBitmap(image)
            }
        }*/
    }

    fun tryValidateTestBlockName() {
        if(binding.testPersonNameEditText.text.length >= MINIMUM_TEST_BLOCK_NAME_LENGTH) {
            binding.startBtn.text = "NAPPI"
            binding.startBtn.visibility = VISIBLE
        } else {
            binding.startBtn.text = ""
            binding.startBtn.visibility = INVISIBLE
        }
    }

    fun startBtnPressed(view: View) {

    }
}