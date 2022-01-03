package ipca.project.ipchatv2.Calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applandeo.materialcalendarview.CalendarView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.*
import android.R
import android.content.Intent
import android.widget.ImageButton
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import ipca.project.ipchatv2.Models.CalendarModel
import ipca.project.ipchatv2.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    lateinit var imageButtonAdd: ImageButton
    lateinit var binding: FragmentCalendarBinding
    lateinit var calendarView : CalendarView
    val adapter = GroupAdapter<ViewHolder>()
    val dateList: ArrayList<CalendarModel> = ArrayList()
    val events: MutableList<EventDay> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Get Layout
        binding = FragmentCalendarBinding.inflate(layoutInflater)

        getCurrentCalendar()

        imageButtonAdd = binding.buttonAdd
        imageButtonAdd.bringToFront()
        calendarView = binding.calendarView

        binding.recyclerView.adapter = adapter

        displayEventIcons()

        binding.buttonAdd.setOnClickListener {

            val intent = Intent(requireContext(), NewEventActivity::class.java)
            startActivity(intent)

        }

        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                addEventRows(clickedDayCalendar)
            }
        })

        return binding.root
    }

    private fun displayEventIcons() {

        dateList.forEach {

            val calendar = Calendar.getInstance()
            calendar.time = it.date!!
            events.add(EventDay(calendar, R.drawable.presence_online))

        }

        calendarView.setEvents(events)

    }

    fun getCurrentCalendar(){

        //val uid = FirebaseAuth.getInstance().uid
        val uid = "PNRsmOMiaQZYgstQh01GijHmENm1"
        val ref = Firebase.firestore.collection("Calendar").document(uid)
            .collection("Meetings")

        ref.addSnapshotListener { value, error ->

            for (document in value!!.documents){

                val calendar = document.toObject(CalendarModel::class.java)

                dateList.add(calendar!!)

            }

            displayEventIcons()

        }
    }

    fun addEventRows(date: Calendar){

        adapter.clear()

        dateList.forEach {

            val calendarDate = Calendar.getInstance()
            calendarDate.time = it.date!!

            val dayFirebase = calendarDate.get(Calendar.DAY_OF_MONTH)
            val monthFirebase = calendarDate.get(Calendar.MONTH)
            val yearFirebase = calendarDate.get(Calendar.YEAR)

            val dayCalendar = date.get(Calendar.DAY_OF_MONTH)
            val monthCalendar = date.get(Calendar.MONTH)
            val yearCalendar = date.get(Calendar.YEAR)

            if(dayFirebase == dayCalendar && monthFirebase == monthCalendar
                && yearFirebase == yearCalendar)
                adapter.add(CalendarRow(it))

        }

    }


}