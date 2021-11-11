package ipca.project.ipchatv2.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R

class ChatActivity : AppCompatActivity() {

    lateinit var toUser : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        toUser = intent.getParcelableExtra<User>("User")!!

        supportActionBar?.title = toUser.username
    }
}