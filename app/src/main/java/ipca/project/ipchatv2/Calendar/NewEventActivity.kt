package ipca.project.ipchatv2.Calendar

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import ipca.project.ipchatv2.Models.CalendarModel
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.Models.PrivateChannel
import ipca.project.ipchatv2.databinding.ActivityNewEventBinding
import java.util.*

class NewEventActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewEventBinding
    var time : Date? = null

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val calendarId = intent.getStringExtra("calendarId")
        val channelType = intent.getStringExtra("channelType")


        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.textViewSelectDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val month: Int = calendar.get(Calendar.MONTH)
            val year: Int = calendar.get(Calendar.YEAR)

            val picker = DatePickerDialog(this,
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

            val editTextTitle = binding.editTextTitle
            val editTextLocal = binding.editTextLocal
            val editTextDescription = binding.editTextDescription


            if(editTextTitle.text.isEmpty() || editTextLocal.text.isEmpty() ||
                editTextDescription.text.isEmpty() || time == null){
                Toast.makeText(this, "Por favor, preencha todos os dados", Toast.LENGTH_SHORT).show()
            }else{

                val calendar = CalendarModel(null, time, editTextTitle.text.toString(),
                                            currentUser.uid, editTextDescription.text.toString(),
                                            editTextLocal.text.toString())

                if(channelType != null){

                    if(channelType == "group"){

                        db.collection("groupChannels")
                            .document(calendarId!!)
                            .get()
                            .addOnSuccessListener { result ->

                                val channel = result.toObject(GroupChannel::class.java)

                                val userIds = channel!!.userIds

                                val channelCalendar = db.collection("Calendar")
                                    .document(calendarId)
                                    .collection("Meetings")

                                    channelCalendar.add(calendar)
                                    .addOnSuccessListener { calendarObject ->

                                        channelCalendar.document(calendarObject.id)
                                            .update(mapOf("calendarId" to calendarObject.id))

                                        calendar.calendarId = calendarObject.id

                                        Toast.makeText(this, "Evento adicionado com sucesso", Toast.LENGTH_SHORT).show()

                                        userIds!!.forEachIndexed { index, id ->

                                            db.collection("Calendar")
                                                .document(id)
                                                .collection("Meetings")
                                                .document(calendarObject.id)
                                                .set(calendar)
                                                .addOnSuccessListener {

                                                    if(index == (userIds.size - 1))
                                                        finish()

                                                }
                                        }

                                    }.addOnFailureListener { error ->

                                        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

                                    }

                            }

                    }else{

                        db.collection("privateChannels")
                            .document(calendarId!!)
                            .get()
                            .addOnSuccessListener { result ->

                                val channel = result.toObject(GroupChannel::class.java)

                                val userIds = channel!!.userIds

                                val channelCalendar = db.collection("Calendar")
                                    .document(calendarId)
                                    .collection("Meetings")

                                channelCalendar.add(calendar)
                                    .addOnSuccessListener { calendarObject ->

                                        channelCalendar.document(calendarObject.id)
                                            .update(mapOf("calendarId" to calendarObject.id))

                                        calendar.calendarId = calendarObject.id

                                        Toast.makeText(this, "Evento adicionado com sucesso", Toast.LENGTH_SHORT).show()

                                        userIds!!.forEachIndexed { index, id ->

                                            db.collection("Calendar")
                                                .document(id)
                                                .collection("Meetings")
                                                .document(calendarObject.id)
                                                .set(calendar)
                                                .addOnSuccessListener {

                                                    if(index == (userIds.size - 1))
                                                        finish()

                                                }
                                        }

                                    }.addOnFailureListener { error ->

                                        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

                                    }
                            }

                    }
                }else{

                    val channelCalendar = db.collection("Calendar")
                        .document(calendarId!!)
                        .collection("Meetings")

                    channelCalendar.add(calendar)
                        .addOnSuccessListener { calendarObject ->

                            channelCalendar.document(calendarObject.id)
                                .update(mapOf("calendarId" to calendarObject.id))

                            Toast.makeText(
                                this,
                                "Evento adicionado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }

                }
            }

        }
    }
}