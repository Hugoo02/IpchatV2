package ipca.project.ipchatv2.Calendar

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import ipca.project.ipchatv2.Models.CalendarModel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.ProfileFragment
import ipca.project.ipchatv2.databinding.ActivityEditEventBinding
import kotlinx.android.synthetic.main.activity_edit_event.*
import java.util.*

class EditEventActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditEventBinding
    var time : Date? = null
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var currentEvent = intent.getStringExtra("calendarId")

        supportActionBar?.hide()

        var title = binding.editTextTitle
        var local = binding.editTextLocal
        var description = binding.editTextDescription


        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.textViewSelectDate.setOnClickListener {

            val calendar = Calendar.getInstance()

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
            val intent = Intent(this, CalendarFragment::class.java)
            startActivity(intent)
            finish()
        }

        binding.buttonDelete.setOnClickListener {
            deleteCurrentEvent()
            Toast.makeText(this, "Evento eliminado com sucesso!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CalendarFragment::class.java)
            startActivity(intent)
            finish()
        }
    }



    private fun editCurrentEvent() {

    }

    private fun deleteCurrentEvent(){

    }
}
