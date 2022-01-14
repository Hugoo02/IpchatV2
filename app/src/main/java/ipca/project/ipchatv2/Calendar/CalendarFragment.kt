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
import android.graphics.RectF

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
    var channelType : String? = null

    var yDown = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            calendarId = it.getString("calendarId")
            channelType = it.getString("channelType")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Get Layout
        binding = FragmentCalendarBinding.inflate(layoutInflater)

        if(channelType == null){
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
            intent.putExtra("channelType", channelType)
            startActivity(intent)
            getCurrentCalendar()

        }

        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                addEventRows(clickedDayCalendar)
            }
        })

        imageViewResizeCalendar.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {

                        yDown = event.y

                    }

                    MotionEvent.ACTION_MOVE ->{


                        val yMoved = event.y

                        val distanceY = yMoved - yDown

                        if(imageViewResizeCalendar.y + distanceY < 0)
                            imageViewResizeCalendar.y = 0f
                        else if (imageViewResizeCalendar.y + distanceY > calendarView.height - (imageViewResizeCalendar.height + 20f))
                            imageViewResizeCalendar.y = calendarView.height - (imageViewResizeCalendar.height + 20f)
                        else
                            imageViewResizeCalendar.y = imageViewResizeCalendar.y + distanceY

                        calendarView.y = imageViewResizeCalendar.y - (calendarView.height -
                                (imageViewResizeCalendar.height + 20))

                        binding.recyclerView.y = calendarView.y + (calendarView.height + 5)

                        val params = binding.recyclerView.layoutParams

                        val oneRect = calculateRectOnScreen(binding.recyclerView)
                        val otherRect = calculateRectOnScreen(binding.constraintTeste)

                        val distance = Math.abs(otherRect.bottom - oneRect.top)

                        params.height = distance.toInt()

                        binding.recyclerView.layoutParams = params



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

    fun calculateRectOnScreen(view: View): RectF {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return RectF(
            location[0].toFloat(),
            location[1].toFloat(),
            (location[0] + view.measuredWidth).toFloat(),
            (location[1] + view.measuredHeight).toFloat()
        )
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

        val ref = db.collection("Calendar").document(calendarId!!)
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