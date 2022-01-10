package ipca.project.ipchatv2.Chat

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.Notifications.NotificationData
import ipca.project.ipchatv2.Notifications.PushNotification
import ipca.project.ipchatv2.Notifications.RetrofitInstance
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

const val TOPIC = "/topics/myTopic2"

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    val adapter = GroupAdapter<ViewHolder>()

    var groupId : String? = null
    var channelType: String? = null
    //var otherUserToken: String? = null

    val db = FirebaseFirestore.getInstance()

    val currentUser = FirebaseAuth.getInstance().uid

    val messages : MutableList<ChatMessage> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewChat.adapter = adapter
        binding.recyclerViewChat.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        var otherUserToken = "AAAAAA"

        groupId = intent.getStringExtra("groupId")
        channelType = intent.getStringExtra("channelType")
        otherUserToken = intent.getStringExtra("token").toString()


        println(otherUserToken)

        listenForMessages()

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding.buttonSend.setOnClickListener {

            db.collection("User")
                .document(currentUser!!)
                .get()
                .addOnSuccessListener { result->

                    val user = result.toObject(User::class.java)
                    val title = "teste"
                    val groupImage = user!!.imageURL.toString()

                    println(user!!.imageURL)
                    val message = editTextMessage.text.toString()
                    val recipientToken = otherUserToken
                    if(title.isNotEmpty() && message.isNotEmpty() && recipientToken!!.isNotEmpty()) {
                        PushNotification(
                            NotificationData(title, message, groupImage),
                            recipientToken
                        ).also {
                            sendNotification(it)
                        }
                    }
                    performSendMessage()
                }






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

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(ContentValues.TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(ContentValues.TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(ContentValues.TAG, e.toString())
        }
    }
}