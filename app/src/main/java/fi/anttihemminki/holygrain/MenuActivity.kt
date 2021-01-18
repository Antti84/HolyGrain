package fi.anttihemminki.holygrain

import android.os.Bundle
import fi.anttihemminki.holygrain.databinding.ActivityMenuBinding

class MenuActivity : HolyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMenuBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}