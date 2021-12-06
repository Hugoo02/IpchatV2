package ipca.project.ipchatv2.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import ipca.project.ipchatv2.MainActivity
import ipca.project.ipchatv2.R

class ConfirmLoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: EditText
    private lateinit var btnNext: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_login)

        val bundle = intent.extras

        bundle?.let {
            email = it.getString("email").toString()
        }

        password = findViewById(R.id.editTextPassword)
        btnNext = findViewById(R.id.buttonLogin)

        auth = Firebase.auth


        btnNext.setOnClickListener {

            val pass = password.text.toString()
            login(email, pass)
        }
    }

    private fun login(email: String, password: String){


        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }.addOnFailureListener{

            Toast.makeText(this, "Email, ou palavra passe estão incorretos!", Toast.LENGTH_SHORT).show()

        }
    }
}

