package fi.anttihemminki.holygrain

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.Face
import fi.anttihemminki.holygrain.facedistance.*

abstract class CameraActivity : HolyActivity(), HolyCameraReceiveImageInterface {

    lateinit var cameraView: ImageView
    var testTextView: TextView? = null

    var freezeImage = false

    lateinit var camera: HolyCamera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        camera = HolyCamera(this)
        startCamera()
    }

    fun startCamera() {
        camera.startCamera()
    }

    override fun onDestroy() {
        camera.shutdown()
        super.onDestroy()
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

    /*@SuppressLint("SetTextI18n")
    override fun receiveFaceImage(image: ImageProxy, time: Long) {
        if (time > 0) {
            val now = System.currentTimeMillis()
            if(testTextView != null)
                testTextView!!.text = "$now - $time = ${now - time}"
        }
        if(!freezeImage)
            setImageToLayout(image)
    }

    var drawDotsToImage = false
    override fun receiveFaceImageAndData(image: ImageProxy, faces: MutableList<Face>, time: Long) {

        if(drawDotsToImage) {
            var bmp = imageProxyToBitmap(image)
            if (bmp != null) {
                for (face in faces) {
                    bmp = drawFacePointsToBitmap(bmp!!, face)
                }
                bmp = bmp!!.flip(-1f, 1f, bmp.width / 2f, bmp.height / 2f)
                runOnUiThread {
                    if(!freezeImage)
                        cameraView.setImageBitmap(bmp)
                }
            }
        } else {
            receiveFaceImage(image, -1)
        }

        //
        receiveFaceData(faces, time)
    }

    /*@SuppressLint("SetTextI18n")
    override */open fun receiveFaceData(faces: MutableList<Face>, time: Long) {
        val now = System.currentTimeMillis()
        val s = "$now - $time = ${now - time}"
        if(testTextView != null)
            testTextView!!.text = s + "\n" + "Num faces: ${faces.size}"
    }*/
}