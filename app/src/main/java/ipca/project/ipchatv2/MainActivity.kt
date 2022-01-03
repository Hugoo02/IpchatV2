package ipca.project.ipchatv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import ipca.project.ipchatv2.Authentication.LoginActivity
import ipca.project.ipchatv2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

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

    }
}