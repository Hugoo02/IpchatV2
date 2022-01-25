package ipca.project.ipchatv2.Calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ipca.project.ipchatv2.databinding.ActivityNewEventBinding
import android.R
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class CalendarActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val calendarId = intent.getStringExtra("calendarId")
        val channelType = intent.getStringExtra("channelType")

        val calendarFragment = CalendarFragment()
        val bundle = Bundle()

        bundle.putString("calendarId", calendarId)
        bundle.putString("channelType", channelType)

        calendarFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .add(R.id.content, calendarFragment).commit()
    }
}