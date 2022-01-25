package ipca.project.ipchatv2

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import ipca.project.ipchatv2.Authentication.LoginActivity
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.Notifications.FirebaseService
import ipca.project.ipchatv2.Utils.Utils
import ipca.project.ipchatv2.databinding.ActivityMainBinding
import android.preference.PreferenceManager

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate







class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    val IS_DARK = "IS_DARK"

    override fun attachBaseContext(baseContext: Context?) {
        super.attachBaseContext(baseContext)
        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val isDark = prefs.getBoolean(IS_DARK, false)
        if (isDark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) else AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        val navController = findNavController(R.id.fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.calendarFragment, R.id.profileFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)

        binding.bottomNavigation.setupWithNavController(navController)

        Utils.connectionLiveData(this)

        checkAuthentication()



    }

    private fun checkAuthentication() {

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null){

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
        else{
            checkDevice()
        }

    }


    private fun checkDevice(){

        var userReference = db.collection("User").document(auth.currentUser!!.uid)

        userReference.get().addOnSuccessListener { result ->
            val user = result.toObject(User::class.java)

            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                FirebaseService.token = it.token


                if(user!!.token != it.token){
                    user.token = it.token


                    userReference.set(user)
                }
            }
        }
    }





    override fun onPause() {
        super.onPause()


        var userReference = db.collection("User").document(auth.currentUser!!.uid)
        val uid = FirebaseAuth.getInstance().uid

        userReference.get().addOnSuccessListener { result ->
            val user = result.toObject(User::class.java)

            val updateUser = User(uid.toString(), user!!.username, user!!.course, user!!.email,
                user!!.address, user!!.imageURL, user!!.student_number, user!!.year, user!!.gender, user!!.biography, user!!.token, false)

            db.collection("User").document(uid.toString()).set(updateUser)

        }
    }


    override fun onResume() {
        super.onResume()

        var userReference = db.collection("User").document(auth.currentUser!!.uid)
        val uid = FirebaseAuth.getInstance().uid

        userReference.get().addOnSuccessListener { result ->
            val user = result.toObject(User::class.java)

            val updateUser = User(uid.toString(), user!!.username, user!!.course, user!!.email,
                user!!.address, user!!.imageURL, user!!.student_number, user!!.year, user!!.gender, user!!.biography, user!!.token, true)

            db.collection("User").document(uid.toString()).set(updateUser)

        }
    }

}