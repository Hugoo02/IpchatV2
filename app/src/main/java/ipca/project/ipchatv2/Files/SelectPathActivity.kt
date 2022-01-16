package ipca.project.ipchatv2.Files

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import ipca.project.ipchatv2.databinding.ActivitySelectPathBinding

class SelectPathActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectPathBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectPathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.linearLayoutDownloads.setOnClickListener {

            val path = Environment.getExternalStorageDirectory().absolutePath.toString()

            val intent = Intent(this, InsidePathActivity::class.java)
            intent.putExtra("requestPath", path)
            startActivity(intent)

        }

        println("path = " + Environment.getExternalStorageDirectory().absolutePath)


        requestPermissions()

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }
}