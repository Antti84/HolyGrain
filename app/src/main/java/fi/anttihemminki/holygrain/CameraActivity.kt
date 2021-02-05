package fi.anttihemminki.holygrain

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.*
import fi.anttihemminki.holygrain.databinding.CameraActivityBinding
import fi.anttihemminki.holygrain.facedistance.*

open class CameraActivity : HolyActivity(), DistanceImageAndDataReceiverInterface {

    lateinit var binding: CameraActivityBinding

    private lateinit var distanceMeter: DistanceMeter

    lateinit var cameraView: ImageView
    lateinit var testTextView: TextView

    var freezeFaceImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding = CameraActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraView = binding.cameraImageView
        testTextView = binding.testTextView

        distanceMeter = DistanceMeter(this)
        distanceMeter.imageDataReceiver = this
        distanceMeter.startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        distanceMeter.shutdown()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    fun setImageToLayout(imageProxy: ImageProxy) {
        var bmp = imageProxyToBitmap(imageProxy)
        if(bmp != null) {
            bmp = bmp.flip(-1f, 1f, bmp.width/2f, bmp.height/2f)
            this.runOnUiThread {
                cameraView.setImageBitmap(bmp)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun receiveFaceImage(image: ImageProxy, time: Long) {
        if (time > 0) {
            val now = System.currentTimeMillis()
            testTextView.text = "$now - $time = ${now - time}"
        }
        if(!freezeFaceImage)
            setImageToLayout(image)
    }

    var drawDotsToImage = false
    override fun receiveFaceImageAndData(image: ImageProxy, faces: MutableList<Face>, time: Long) {

        if(!freezeFaceImage && drawDotsToImage) {
            var bmp = imageProxyToBitmap(image)
            if (bmp != null) {
                for (face in faces) {
                    bmp = drawFacePointsToBitmap(bmp!!, face)
                }
                bmp = bmp!!.flip(-1f, 1f, bmp.width / 2f, bmp.height / 2f)
                runOnUiThread {
                    cameraView.setImageBitmap(bmp)
                }
            }
        } else {
            receiveFaceImage(image, -1)
        }

        //
        receiveFaceData(faces, time)
    }

    @SuppressLint("SetTextI18n")
    override fun receiveFaceData(faces: MutableList<Face>, time: Long) {
        val now = System.currentTimeMillis()
        val s = "$now - $time = ${now - time}"
        testTextView.text = s + "\n" + "Num faces: ${faces.size}"
    }
}