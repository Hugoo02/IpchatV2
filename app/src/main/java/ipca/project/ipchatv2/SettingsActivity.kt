package ipca.project.ipchatv2

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ipca.project.ipchatv2.Authentication.LoginActivity
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.FragmentCalendarBinding

class SettingsActivity: AppCompatActivity() {


    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().uid
    lateinit var circleImageView: CircleImageView
    lateinit var buttonBack: Button
    lateinit var changeTheme: Button
    lateinit var logout: Button
    lateinit var username : TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        buttonBack = findViewById(R.id.buttonBack)
        changeTheme = findViewById(R.id.buttonDarkMode)
        logout = findViewById(R.id.buttonLogout)

        mAuth = FirebaseAuth.getInstance()


        buttonBack.setOnClickListener{
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
        }


        logout.setOnClickListener {

            var builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.cancel))
            builder.setTitle(getString(R.string.logout))
            builder.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener{ dialog, id ->

                mAuth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

            })
            builder.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener{ dialog, id ->


            })
            var alert = builder.create()
            alert.show()
        }



        supportActionBar!!.hide()
    }
}