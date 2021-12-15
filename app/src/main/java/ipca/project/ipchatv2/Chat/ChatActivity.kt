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
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.ktx.toObject
import ipca.project.ipchatv2.R
import java.io.IOException


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
        binding.recyclerViewChat.addItemDecoration(DividerItemDecoration(this, 0))

        groupId = intent.getStringExtra("groupId")
        channelType = intent.getStringExtra("channelType")

        listenForMessages()

        adapter.setOnItemLongClickListener { item, view ->

            var text: String? = null

            if(item.layout == R.layout.row_text_message_to){

                val row = item as ChatToItem
                text = row.message.text

            } else{

                val row = item as ChatFromItem
                text = row.message.text

            }

            println(text)

            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", item.toString())
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()

            return@setOnItemLongClickListener true

        }

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

            refSendMessage = db.collection("privateChannels")
                .document(groupId!!)
                .collection("messages")

            refSendLastMessage = db.collection("privateChannels")
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

            println("groupId = $groupId")

            refMessages = db.collection("privateChannels")
                .document(groupId!!)
                .collection("messages")

        }

        refMessages!!.addSnapshotListener{result, e ->

            messages.clear()
            adapter.clear()

            for (doc in result!!){

                messages.add(doc.toObject(ChatMessage::class.java))

            }

            messages.sortBy{it.time}

            messages.forEach {

                if(it.senderId == null)
                    adapter.add(FirstMessage(it))
                else if (it.senderId == currentUser)
                    adapter.add(ChatFromItem(it))
                else
                    adapter.add(ChatToItem(it))

            }

            recyclerViewChat.scrollToPosition(adapter.itemCount - 1)

        }

    }
}