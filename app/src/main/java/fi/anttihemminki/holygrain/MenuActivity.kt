package fi.anttihemminki.holygrain

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import fi.anttihemminki.holygrain.databinding.ActivityMenuBinding

class MenuActivity : HolyActivity() {

    lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.tutkIdInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.i("TAGGI", s.toString())
            }

            override fun afterTextChanged(s: Editable?) { }

        })
    }

    fun goToDistanceDataCollection(view: View) {
        binding.button.isEnabled = false
        val dId = binding.tutkIdInput.text.toString().toInt()
        val intent = Intent(this, DistanceMeterActivity::class.java)
        intent.putExtra("DistanceStudyId", dId)
        startActivity(intent)
    }
}