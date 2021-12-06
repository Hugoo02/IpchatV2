package ipca.project.ipchatv2.Calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.Calendar
import ipca.project.ipchatv2.Utils
import ipca.project.ipchatv2.databinding.FragmentCalendarBinding
import java.sql.Time
import java.util.*

class CalendarFragment : Fragment() {

    lateinit var binding: FragmentCalendarBinding
    lateinit var calendarView : CalendarView
    val adapter = GroupAdapter<ViewHolder>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        //Get Layout
        binding = FragmentCalendarBinding.inflate(layoutInflater)
        calendarView = binding.calendarView

        binding.recyclerView.adapter = adapter

        getCurrentCalendar()


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

                println("Esta e a data" + timeStampToDate)

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



                var teste =  Utils.markEventInCalendar(calendar.date as Date)

                println("cheguei" + teste)

            }

        }
    }


    companion object {
        val FIREBASEURL = "https://messenger-28931-default-rtdb.europe-west1.firebasedatabase.app/"
    }

}