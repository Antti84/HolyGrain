package fi.anttihemminki.holygrain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View

const val HOLY_TAG = "HolyGrain"

lateinit var ACTIVE_ACTIVITY: AppCompatActivity

abstract class HolyActivity : AppCompatActivity() {

    val screen = Screen()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ACTIVE_ACTIVITY = this
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()

        getScreenProps()
    }


    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun getScreenProps() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        screen.width = dm.widthPixels
        screen.height = dm.heightPixels
        screen.ppi = dm.densityDpi
        Log.i(HOLY_TAG, "Screen: ${screen.width}x${screen.height}, ppi: ${screen.ppi}")
    }
}