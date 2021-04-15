package fi.anttihemminki.holygrain.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import fi.anttihemminki.holygrain.HolyActivity
import fi.anttihemminki.holygrain.databinding.ActivityGameMenuBinding

class GameMenuActivity : HolyActivity() {

    lateinit var binding: ActivityGameMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, HolyGameActivity::class.java)
        intent.putExtra("DistanceStudyId", 6666)
        startActivity(intent)

        binding = ActivityGameMenuBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val vList = arrayListOf<Visus>()
        for(visus in decimalVisusList) {
            vList.add(Visus(visus.visus))
        }
        val visuses = vList.toArray()

        val visusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, visuses)
        binding.spinner.adapter = visusAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedVisus: Visus = binding.spinner.selectedItem as Visus

                var text = "Etäisyysrajat etäisyyksillä:"
                for(dist in listOf(25, 30, 35, 40, 45)) {
                    text += "\n$dist: ${selectedVisus.getDistanceRange(dist.toDouble())}"
                }
                binding.spinnerText.text = text//binding.spinner.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                binding.spinnerText.text = ""
            }

        }

        val deviceText = "$screen"
        binding.gameMenuDeviceText.text = deviceText
    }
}

