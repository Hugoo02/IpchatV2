package ipca.project.ipchatv2.Chat

import android.app.Activity
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.databinding.ActivityShowFilesBinding
import ipca.project.ipchatv2.databinding.ActivityShowPhotoCloserBinding
import java.util.*
import android.util.DisplayMetrics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import ipca.project.ipchatv2.Models.User
import kotlin.math.roundToInt


class ShowPhotoCloserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowPhotoCloserBinding

    val db = FirebaseFirestore.getInstance()

    var groupId : String? = null
    var channelType : String? = null
    var message : ChatMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowPhotoCloserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        groupId = intent.getStringExtra("groupId")

        //Utilizar quando for necessário apagar mensagens
        channelType = intent.getStringExtra("channelType")
        message = intent.getParcelableExtra("message")

        binding.toolbar.bringToFront()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        val imageViewPhotoCloser = binding.imageViewPhotoCloser

        Picasso.get().load(message!!.text).resize(width, height).centerCrop().into(imageViewPhotoCloser)

        binding.buttonBack.setOnClickListener {
            finish()
        }

        db.collection("User")
            .document(message!!.senderId!!)
            .get()
            .addOnSuccessListener { result ->

                val user = result.toObject(User::class.java)

                binding.textViewSendBy.text =  "Enviado por ${user!!.username}"

            }

    }

}