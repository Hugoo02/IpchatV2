package ipca.project.ipchatv2.Calendar

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ipca.project.ipchatv2.databinding.ActivityNewEventBinding
import java.util.*

class NewEventActivity : AppCompatActivity() {
    lateinit var binding: ActivityNewEventBinding
    var time : Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewSelectDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            var day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            var month: Int = calendar.get(Calendar.MONTH)
            var year: Int = calendar.get(Calendar.YEAR)


            val picker = DatePickerDialog(this,
                { view, year, monthOfYear, dayOfMonth ->

                    binding.textViewSelectDate.text = "$dayOfMonth/${monthOfYear + 1}/$year"
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.YEAR, year)
                    time = calendar.time

                },
                day,
                month,
                year
            )
            picker.show()

        }

        binding.imageButtonSave.setOnClickListener {

            val editTextTitle = binding.editTextTitle
            val editTextLocal = binding.editTextLocal
            val editTextDescription = binding.editTextDescription

            if(editTextTitle.text.isEmpty() || editTextLocal.text.isEmpty() ||
                editTextDescription.text.isEmpty() || time != null){
                Toast.makeText(this, "Por favor, preencha todos os dados", Toast.LENGTH_SHORT).show()
            }else{



            }

        }

        binding
    }
}