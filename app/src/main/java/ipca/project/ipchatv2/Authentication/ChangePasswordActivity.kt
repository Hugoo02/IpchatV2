package ipca.project.ipchatv2.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import ipca.project.ipchatv2.R

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var currentPassword: EditText
    lateinit var newPassword:EditText
    lateinit var newPassword2:EditText
    lateinit var btnChangePass: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        currentPassword = findViewById(R.id.editTextEmail)
        newPassword = findViewById(R.id.editTextPassword)
        newPassword2 = findViewById(R.id.newPasswordRepeat)
        btnChangePass = findViewById(R.id.buttonLogin)

        btnChangePass.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword(){
        if(currentPassword.text.isNotEmpty() && newPassword.text.isNotEmpty() && newPassword2.text.isNotEmpty())
        {
            if (newPassword.text.toString().equals((newPassword2.text.toString())))
            {
                val user = auth.currentUser

                if(user != null && user.email != null)
                {
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, currentPassword.text.toString())

                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(this, "Reautenticação completa.", Toast.LENGTH_SHORT).show()
                                user?.updatePassword(newPassword.text.toString())
                                    ?.addOnCompleteListener { task ->
                                        if(task.isSuccessful){
                                            Toast.makeText(this, "palavra-passe mudada com sucesso.", Toast.LENGTH_SHORT).show()
                                            auth.signOut()
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        }
                                    }
                            }
                        }
                }
                else{
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            else
            {
                Toast.makeText(this, "Palavra-passe imcompatível.", Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            Toast.makeText(this, "Por favor preencha todos os campos.", Toast.LENGTH_SHORT).show()
        }
    }
}