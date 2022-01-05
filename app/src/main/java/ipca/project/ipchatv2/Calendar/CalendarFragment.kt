package ipca.project.ipchatv2.Calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.*
import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginStart
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.FirebaseFirestoreKtxRegistrar
import ipca.project.ipchatv2.Models.CalendarModel
import ipca.project.ipchatv2.databinding.FragmentCalendarBinding
import android.widget.RelativeLayout




class CalendarFragment : Fragment() {

    lateinit var imageButtonAdd: ImageButton
    val db = FirebaseFirestore.getInstance()
    lateinit var binding: FragmentCalendarBinding
    lateinit var calendarView : CalendarView
    val adapter = GroupAdapter<ViewHolder>()
    val dateList: ArrayList<CalendarModel> = ArrayList()
    val events: MutableList<EventDay> = ArrayList()

    val currentUser = FirebaseAuth.getInstance()

    var calendarId : String? = null

    var yDown = 0f

    var initialY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Get Layout
        binding = FragmentCalendarBinding.inflate(layoutInflater)

        if(calendarId == null){
            calendarId = currentUser.uid
            binding.buttonBack.visibility = View.GONE

            val layoutParams = binding.textViewCalendar.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.marginStart = 20.toDp(requireContext())
            binding.textViewCalendar.layoutParams = layoutParams

        }

        imageButtonAdd = binding.imageButtonAdd
        imageButtonAdd.bringToFront()
        calendarView = binding.calendarView
        val imageViewResizeCalendar = binding.imageViewResizeCalendar

        val calendarMinDate = Calendar.getInstance()
        calendarMinDate.set(Calendar.YEAR, calendarMinDate.get(Calendar.YEAR) - 5)

        val calendarMaxDate = Calendar.getInstance()
        calendarMaxDate.set(Calendar.YEAR, calendarMaxDate.get(Calendar.YEAR) + 5)

        calendarView.setMinimumDate(calendarMinDate)
        calendarView.setMaximumDate(calendarMaxDate)

        binding.recyclerView.adapter = adapter

        displayEventIcons()

        imageButtonAdd.setOnClickListener {

            val intent = Intent(requireContext(), NewEventActivity::class.java)
            intent.putExtra("calendarId", calendarId)
            startActivity(intent)
            getCurrentCalendar()

        }


        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                addEventRows(clickedDayCalendar)
            }
        })

        println("calendar.y = " + calendarView.y)
        println("height = " + calendarView.height)
        imageViewResizeCalendar.y = 0f
        println("top " + calendarView.top)
        println("top " + imageViewResizeCalendar.top)
        println("calendar height = ${calendarView.top + calendarView.bottom}")
        println("calendar height = ${calendarView.height}")

        var initialCalendarHeight = 1800
        var initialCalendarY = calendarView.y
        var initialImageViewY = imageViewResizeCalendar.y

        imageViewResizeCalendar.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {

                        yDown = event.y
                        println("yDown = " + yDown)

                    }

                    MotionEvent.ACTION_MOVE ->{

                        val yMoved = event.y

                        println("yMoved = " + yMoved)

                        val distanceY = yMoved - yDown

                        println(imageViewResizeCalendar.y)

                        imageViewResizeCalendar.y = imageViewResizeCalendar.y + distanceY

                        val params = calendarView.layoutParams

                        val heightt = (params.height * yMoved) / yDown
                        initialCalendarHeight -= (distanceY * 4).toInt()

                        initialCalendarHeight += distanceY.toInt()
                        println("calendar height = $initialCalendarHeight")

                        //params.height -= ((imageViewResizeCalendar.y + distanceY)).toInt().toDp(requireContext())
                        //params.height = 200 * 4
                        //params.height = height.toInt()
                        //calendarView.layoutParams = params

                    }
                }

                return true
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getCurrentCalendar()
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

        dateList.clear()

        val uid = FirebaseAuth.getInstance().uid

        if(calendarId == null)
            calendarId = uid

        val ref = db.collection("Calendar").document(uid!!)
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

    fun Int.toDp(context: Context):Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
    ).toInt()

}