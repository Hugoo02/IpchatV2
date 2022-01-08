package ipca.project.ipchatv2.Calendar

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ipca.project.ipchatv2.Models.CalendarModel
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

        var calendarId = intent.getStringExtra("calendarId")

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

                val calendar = CalendarModel(time, editTextTitle.text.toString(),
                                            currentUser.uid, editTextDescription.text.toString(),
                                            editTextLocal.text.toString())

                db.collection("Calendar")
                    .document(calendarId!!)
                    .collection("Meetings")
                    .add(calendar)
                    .addOnSuccessListener {

                        Toast.makeText(this, "Evento adicionado com sucesso", Toast.LENGTH_SHORT).show()
                        finish()

                    }.addOnFailureListener { error ->

                        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()

                    }


            }

        }
    }
}