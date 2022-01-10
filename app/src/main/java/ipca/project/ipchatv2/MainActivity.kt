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
import ipca.project.ipchatv2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    private var currentUser = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.calendarFragment, R.id.profileFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)

        binding.bottomNavigation.setupWithNavController(navController)

        Utils.connectionLiveData(this)

        checkAuthentication()

        checkDevice()

    }

    private fun checkDevice(){

        var userReference = db.collection("User").document(currentUser.uid!!)

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

    private fun checkAuthentication() {

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null){

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

    }
}