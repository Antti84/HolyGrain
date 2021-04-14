package fi.anttihemminki.holygrain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import fi.anttihemminki.holygrain.databinding.ActivityGameMenuBinding
import fi.anttihemminki.holygrain.game.HolyGameActivity
import kotlin.math.round
import kotlin.math.tan

class GameMenuActivity : HolyActivity() {

    lateinit var binding: ActivityGameMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, HolyGameActivity::class.java)
        intent.putExtra("DistanceStudyId", 6666)
        startActivity(intent)

        binding = ActivityGameMenuBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val decimalVisuses = arrayOf(2.00, 1.60, 1.25, 1.00, 0.80, 0.63, 0.50, 0.40,
                                     0.32, 0.25, 0.16, 0.125, 0.10, 0.08, 0.06, 0.05)
        val vList = arrayListOf<Visus>()
        for(decimal in decimalVisuses) {
            vList.add(Visus(decimal))
        }
        val visuses = vList.toArray()

        val visusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, visuses)
        binding.spinner.adapter = visusAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedVisus: Visus = binding.spinner.selectedItem as Visus

                var text = "Etäisyysrajat etäisyyksillä:"
                for(dist in listOf(25, 30, 35, 40, 45)) {
                    text += "\n$dist: ${selectedVisus.getDistanceRange(dist.toDouble()).toString()}"
                }
                binding.spinnerText.text = text//binding.spinner.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                binding.spinnerText.text = ""
            }

        }

        var deviceText = "$screen"
        binding.gameMenuDeviceText.text = deviceText
    }
}

data class DistanceRange(val near: Double, val far: Double)

const val gapSizePermissibleError = 0.025
class Visus(private val decimal: Double) {
    private val pixelSizeCm: Double = 2.54 / ACTIVE_ACTIVITY.screen.ppi
    private val minArc: Double = 1.0 / decimal

    override fun toString(): String {
        return "Visus: $decimal, min_arc: $minArc"
    }

    fun getDistanceRange(distance: Double): DistanceRange {
        val tangent = tan(Math.toRadians(1.0/(60*decimal)))

        val gapSize = distance*tangent
        val pixels = round(gapSize/pixelSizeCm)
        val near = pixels*pixelSizeCm*(1.0-gapSizePermissibleError)/tangent
        val far = pixels*pixelSizeCm*(1.0+gapSizePermissibleError)/tangent
        return DistanceRange(near, far)
    }
}