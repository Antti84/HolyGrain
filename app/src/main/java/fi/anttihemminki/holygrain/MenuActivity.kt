package fi.anttihemminki.holygrain

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import fi.anttihemminki.holygrain.databinding.ActivityMenuBinding

val IDMIN = 1000
val IDMAX = 9999

class MenuActivity : HolyActivity() {

    lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val s = TestSetState()
        for(ss in s.states) {
            Log.i("STATE", testSetStateDataToPositionString(ss))
        }
        Log.i("STATES LENGTH", s.states.size.toString())

        binding = ActivityMenuBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.button.isEnabled = false

        binding.tutkIdInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val dId = getIdFromForm()
                binding.button.isEnabled = isIdValid(dId)
            }

            override fun afterTextChanged(s: Editable?) { }

        })
    }

    fun goToDistanceDataCollection(view: View) {
        val dId = getIdFromForm()
        binding.button.isEnabled = false
        if(!isIdValid(dId)) {
            return
        }
        val intent = Intent(this, DistanceMeterActivity::class.java)
        intent.putExtra("DistanceStudyId", dId)
        startActivity(intent)
    }

    fun isIdValid(id: Int): Boolean {
        return id in IDMIN..IDMAX
    }

    fun getIdFromForm(): Int {
        return binding.tutkIdInput.text.toString().toInt()
    }
}