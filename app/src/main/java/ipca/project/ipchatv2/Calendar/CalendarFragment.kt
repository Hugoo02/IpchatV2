package ipca.project.ipchatv2.Calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.Calendar
import ipca.project.ipchatv2.Utils
import java.util.*
import android.R
import android.annotation.SuppressLint
import android.widget.ImageButton
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.utils.calendar
import ipca.project.ipchatv2.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    lateinit var imageButtonAdd: ImageButton
    lateinit var binding: FragmentCalendarBinding
    lateinit var calendarView : CalendarView
    val adapter = GroupAdapter<ViewHolder>()
    val dates: ArrayList<String> = ArrayList()
    val events: MutableList<EventDay> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //Get Layout
        binding = FragmentCalendarBinding.inflate(layoutInflater)
        imageButtonAdd = binding.imageButtonAdd
        imageButtonAdd.bringToFront()
        calendarView = binding.calendarView


        binding.recyclerView.adapter = adapter

        getCurrentCalendar()
        getEventAndDisplayIt()

        adapter.notifyDataSetChanged()

        return binding.root
    }


    fun getCurrentCalendar(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = Firebase.firestore.collection("Calendar").document(uid!!)
            .collection("Meetings")


        ref.get().addOnSuccessListener { documents ->


            for (document in documents)
            {
                val calendar = document.toObject(Calendar::class.java)

                adapter.add(CalendarRow(calendar))

                var timeStampToDate =  Utils.receiveDateFromDatabaseToCalendar(calendar.date as Date)


                getEventAndDisplayIt()
            }

        }
    }

    fun addEventOnCalendar(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = Firebase.firestore.collection("Calendar").document(uid!!)
            .collection("Meetings")

        ref.get().addOnSuccessListener { documents ->


            for (document in documents)
            {
                val calendar = document.toObject(Calendar::class.java)

                adapter.add(CalendarRow(calendar))


            }

        }
    }

    private fun getEventAndDisplayIt(){

        for (date in dates) {
            val calendarEvent: java.util.Calendar = java.util.Calendar.getInstance() // calendar must be here
            val items1 = date.split("-").toTypedArray()
            val year = items1[0].toInt()
            val month = items1[1].toInt()
            val day = items1[2].toInt()
            calendarEvent.set(year, month, day)
            events.add(EventDay(calendarEvent, R.drawable.ic_dialog_email))

        }

        //calendarView.setEvents(events)

    }

    companion object {
        val FIREBASEURL = "https://messenger-28931-default-rtdb.europe-west1.firebasedatabase.app/"
    }

}