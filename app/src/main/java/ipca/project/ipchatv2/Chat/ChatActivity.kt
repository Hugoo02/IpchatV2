package ipca.project.ipchatv2.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Date
import com.google.type.DateTime
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Utils
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    val adapter = GroupAdapter<ViewHolder>()

    var groupId : String? = null
    var channelType: String? = null

    val db = FirebaseFirestore.getInstance()

    val currentUser = FirebaseAuth.getInstance().uid

    val messages : MutableList<ChatMessage> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewChat.adapter = adapter
        binding.recyclerViewChat.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        groupId = intent.getStringExtra("groupId")
        channelType = intent.getStringExtra("channelType")

        listenForMessages()

        binding.buttonSend.setOnClickListener {

            performSendMessage()

        }

        //supportActionBar?.title = toUser.username
    }

    private fun performSendMessage() {

        val text = binding.editTextMessage.text.toString()

        //println(FieldValue.serverTimestamp() as Date)

        var dateTimeString = LocalDateTime.now()
        var ldt = LocalDateTime.parse(dateTimeString.toString())
        println(ldt)

        //val message = ChatMessage(currentUser, text, LocalDateTime.now() as Date)

        if(channelType == "group"){

            val refSendMessage = db.collection("groupChannel")
                .document(groupId!!)
                .collection("messages")

            //refSendMessage.add(message)

        }

    }

    private fun listenForMessages() {

        if(channelType == "group"){

            val refMessages = db.collection("groupChannels")
                .document(groupId!!)
                .collection("messages")

            refMessages.addSnapshotListener{result, e ->

                for (doc in result!!){

                    messages.add(doc.toObject(ChatMessage::class.java))

                }

                println("message 1 antes = ${messages[0]}")
                messages.sortByDescending{it.time}

                messages.forEach {

                    if (it.senderId == currentUser)
                        adapter.add(ChatFromItem(it))
                    else
                        adapter.add(ChatToItem(it))

                }

            }

        }

    }
}