package ipca.project.ipchatv2.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.databinding.ActivityChatBinding

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