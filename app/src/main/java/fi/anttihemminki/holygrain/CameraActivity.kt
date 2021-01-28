package fi.anttihemminki.holygrain

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.*
import fi.anttihemminki.holygrain.databinding.DistanceActivityBinding
import fi.anttihemminki.holygrain.facedistance.*

open class CameraActivity : HolyActivity() {

    private lateinit var binding: DistanceActivityBinding

    private lateinit var distanceMeter: DistanceMeter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding = DistanceActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        distanceMeter = DistanceMeter(this)
        distanceMeter.imageDataReceiver = receiver
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
                binding.cameraImageView.setImageBitmap(bmp)
            }
        }
    }

    private val receiver: DistanceReceiverInterface = object: DistanceImageAndDataReceiverInterface {
        @SuppressLint("SetTextI18n")
        override fun receiveFaceImage(image: ImageProxy, time: Long) {
            if (time > 0) {
                val now = System.currentTimeMillis()
                binding.testTextView.text = "$now - $time = ${now - time}"
            }
            setImageToLayout(image)
        }

        override fun receiveFaceImageAndData(image: ImageProxy, faces: MutableList<Face>, time: Long) {

            var bmp = imageProxyToBitmap(image)
            if(bmp != null) {
                for(face in faces) {
                    bmp = drawFacePointsToBitmap(bmp!!, face)
                }
                bmp = bmp!!.flip(-1f, 1f, bmp.width/2f, bmp.height/2f)
                runOnUiThread {
                    binding.cameraImageView.setImageBitmap(bmp)
                }
            }

            //
            receiveFaceData(faces, time)
        }

        @SuppressLint("SetTextI18n")
        override fun receiveFaceData(faces: MutableList<Face>, time: Long) {
            val now = System.currentTimeMillis()
            val s = "$now - $time = ${now - time}"
            binding.testTextView.text = s + "\n" + "Num faces: ${faces.size}"
        }
    }
}