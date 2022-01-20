package ipca.project.ipchatv2.Calendar

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ipca.project.ipchatv2.MainActivity
import ipca.project.ipchatv2.Models.CalendarModel
import ipca.project.ipchatv2.databinding.ActivityEditEventBinding
import java.util.*

class EditEventActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditEventBinding
    var time : Date? = null
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance()

    var calendarId: String? = null
    var channelType: String? = null
    var event: CalendarModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendarId = intent.getStringExtra("calendarId")
        channelType = intent.getStringExtra("channelType")
        event = intent.extras!!.getParcelable("event")

        binding.editTextTitle.setText(event!!.title)
        binding.editTextLocal.setText(event!!.local)
        binding.editTextDescription.setText(event!!.description)

        val eventCalendar = Calendar.getInstance()
        eventCalendar.time = event!!.date

        val eventCalendarDay = eventCalendar.get(Calendar.DAY_OF_MONTH)
        val eventCalendarMonth = eventCalendar.get(Calendar.MONTH)
        val eventCalendarYear = eventCalendar.get(Calendar.YEAR)

        binding.textViewSelectDate.text = "$eventCalendarDay/${eventCalendarMonth + 1}/$eventCalendarYear"

        supportActionBar?.hide()

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.textViewSelectDate.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.time = event!!.date!!

            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val month: Int = calendar.get(Calendar.MONTH)
            val year: Int = calendar.get(Calendar.YEAR)

            val picker = DatePickerDialog(
                this,
                { view, mYear, monthOfYear, dayOfMonth ->

                    binding.textViewSelectDate.text = "$dayOfMonth/${monthOfYear + 1}/$mYear"
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.YEAR, year)
                    time = calendar.time

                }, year, month, day
            )
            picker.show()
        }

        binding.imageButtonSave.setOnClickListener {
            editCurrentEvent()
            Toast.makeText(this, "Evento editado com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.buttonDelete.setOnClickListener {
            deleteCurrentEvent()
            Toast.makeText(this, "Evento eliminado com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }



    private fun editCurrentEvent() {

        val finalTitle = binding.editTextTitle.text.toString()
        val finalLocal = binding.editTextLocal.text.toString()
        val finalDescription = binding.editTextDescription.text.toString()

        val finalEvent = CalendarModel(time, finalTitle, event!!.createdBy, finalDescription, finalLocal)

        if(channelType == "group") {



        } else if (channelType == "private") {



        } else {

            val calendarRef = db.collection("Calendar")
                .document(currentUser.uid!!)
                .collection("Meetings")

            calendarRef
            .get()
            .addOnSuccessListener { calendarObject ->

                for(doc in calendarObject.documents){

                    val calendar = doc.toObject(CalendarModel::class.java)

                    if(calendar!!.title == event!!.title && calendar.date == event!!.date
                        && calendar.description == event!!.description &&
                        calendar.local == event!!.description){

                        calendarRef.document(doc.id).set(finalEvent)

                    }

                }

            }

        }

    }

    private fun deleteCurrentEvent(){

        if(channelType == "group") {



        } else if (channelType == "private") {



        } else {

            val calendarRef = db.collection("Calendar")
                .document(currentUser.uid!!)
                .collection("Meetings")

            calendarRef
                .get()
                .addOnSuccessListener { calendarObject ->

                    for(doc in calendarObject.documents){

                        val calendar = doc.toObject(CalendarModel::class.java)

                        if(calendar!!.title == event!!.title && calendar.date == event!!.date
                            && calendar.description == event!!.description &&
                            calendar.local == event!!.description){

                            calendarRef.document(doc.id).delete()

                        }

                    }

                }

        }

    }
}