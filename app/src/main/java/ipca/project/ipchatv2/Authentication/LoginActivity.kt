package ipca.project.ipchatv2.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import ipca.project.ipchatv2.MainActivity
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.databinding.ActivityLoginBinding
import ipca.project.ipchatv2.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var btnNext: Button
    private lateinit var auth: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        auth = FirebaseFirestore.getInstance()
        email = findViewById(R.id.editTextEmail)
        btnNext = findViewById(R.id.buttonLogin)


        btnNext.setOnClickListener{

            val mail = email.text.toString()
            searchUser(mail)
        }

    }

    private fun searchUser(mail: String?){
        val db = FirebaseFirestore.getInstance()

        var ref = db.collection("User")


        ref.whereEqualTo("email", mail)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty)
                {
                    Toast.makeText(this@LoginActivity, "User not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                else if (!mail!!.contains("ipca.pt"))
                {
                    Toast.makeText(this, "Email invalido, por favor introduza um email do IPCA!", Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(this@LoginActivity, ConfirmLoginActivity::class.java)
                    intent.putExtra("email", mail)
                    startActivity(intent)
                }
            }
    }
}

    /*
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {

                binding.editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()

            } else {

                binding.editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            }
        }

        binding.buttonLogin.setOnClickListener {

            binding.loginProgressBar.visibility = View.VISIBLE

            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if(email.isEmpty() || password.isEmpty())
            {

                Toast.makeText(this, "Por favor, preencha todos os  dados!", Toast.LENGTH_SHORT).show()

            }
            else if (!email.contains(".ipca.pt"))
            {

                Toast.makeText(this, "Email invalido, por favor introduza um email do IPCA!", Toast.LENGTH_SHORT).show()

            }
            else {

                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }.addOnFailureListener{

                    Toast.makeText(this, "Email, ou palavra passe estão incorretos!", Toast.LENGTH_SHORT).show()

                }

            }

        }
    }
}*/