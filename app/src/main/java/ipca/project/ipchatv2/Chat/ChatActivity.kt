package ipca.project.ipchatv2.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Utils
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList

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
        val date = Calendar.getInstance().time

        val message = ChatMessage(currentUser, text, date, "TEXT")

        var refSendMessage : CollectionReference? = null
        var refSendLastMessage : CollectionReference? = null

        if(channelType == "group"){

            refSendMessage = db.collection("groupChannels")
                .document(groupId!!)
                .collection("messages")

            refSendLastMessage = db.collection("groupChannels")
                .document(groupId!!)
                .collection("lastMessage")

        }else{

            refSendMessage = db.collection("chatChannels")
                .document(groupId!!)
                .collection("messages")

            refSendLastMessage = db.collection("chatChannels")
                .document(groupId!!)
                .collection("lastMessage")

        }

            refSendLastMessage.get().addOnSuccessListener {

                for(doc in it){

                    refSendLastMessage.document(doc.id).delete()

                }

            }

            refSendMessage.add(message).addOnSuccessListener {

                binding.editTextMessage.text.clear()
                binding.recyclerViewChat.scrollToPosition(adapter.itemCount - 1)

                refSendLastMessage.document(it.id).set(message)

            }

    }

    private fun listenForMessages() {

        var refMessages : CollectionReference? = null

        if(channelType == "group") {

            refMessages = db.collection("groupChannels")
                .document(groupId!!)
                .collection("messages")
        }
        else{

            refMessages = db.collection("chatChannels")
                .document(groupId!!)
                .collection("messages")

        }

            refMessages.addSnapshotListener{result, e ->

                messages.clear()
                adapter.clear()

                for (doc in result!!){

                    messages.add(doc.toObject(ChatMessage::class.java))

                }

                messages.sortBy{it.time}

                messages.forEach {

                    if (it.senderId == currentUser)
                        adapter.add(ChatFromItem(it))
                    else
                        adapter.add(ChatToItem(it))

                }

                recyclerViewChat.scrollToPosition(adapter.itemCount - 1)

            }

    }
}