package ipca.project.ipchatv2

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.*
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
    lateinit var buttonBack: ImageButton
    lateinit var changeTheme: Switch
    lateinit var logout: Button
    lateinit var username: TextView
    private lateinit var mAuth: FirebaseAuth

    val IS_DARK = "IS_DARK"

    override fun attachBaseContext(baseContext: Context?) {
        super.attachBaseContext(baseContext)
        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val isDark = prefs.getBoolean(IS_DARK, false)
        if (isDark){
            setDefaultNightMode(MODE_NIGHT_YES)
        }
        else {
            setDefaultNightMode(MODE_NIGHT_NO)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar!!.hide()

        buttonBack = findViewById(R.id.buttonBack)
        changeTheme = findViewById(R.id.buttonDarkMode)
        logout = findViewById(R.id.buttonLogout)

        mAuth = FirebaseAuth.getInstance()

        buttonBack.setOnClickListener {
            val intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this);


        changeTheme.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if (onSwitch) {
                setDefaultNightMode(MODE_NIGHT_YES);
                prefs.edit().putBoolean(IS_DARK, true).apply();
            } else {
                setDefaultNightMode(MODE_NIGHT_NO);
                prefs.edit().putBoolean(IS_DARK, false).apply();
            }

            logout.setOnClickListener {

                var builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.cancel))
                builder.setTitle(getString(R.string.logout))
                builder.setPositiveButton(
                    getString(R.string.yes),
                    DialogInterface.OnClickListener { dialog, id ->

                        mAuth.signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)

                    })
                builder.setNegativeButton(
                    getString(R.string.no),
                    DialogInterface.OnClickListener { dialog, id ->


                    })
                var alert = builder.create()
                alert.show()
                finish()
            }
        }
    }
}