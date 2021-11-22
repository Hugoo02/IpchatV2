package ipca.project.ipchatv2.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.ArrayList

class ChatActivity : AppCompatActivity() {

    lateinit var toUser: User

    var firebaseUser: FirebaseUser? = null
    var topic = ""
    lateinit var reference : CollectionReference

    private val adapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        var toUser = intent.getParcelableExtra<User>("User")!!
        var fromId = FirebaseAuth.getInstance().uid
        var toID = toUser.id

        supportActionBar?.title = toUser.username

        var buttonSendMessage = findViewById<Button>(R.id.buttonSend)
        var editTextMessage = findViewById<EditText>(R.id.editTextMessage)

        //var userId = intent.getStringExtra("userId")
        //var userName = intent.getStringExtra("userName")
        recyclerViewChat.adapter = adapter


        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = Firebase.firestore.collection("PrivateMessages")
            .document("$fromId").collection("$toID")

        buttonSendMessage.setOnClickListener {

            var textMessage: String = editTextMessage.text.toString()

            if (textMessage.isEmpty()) {
                Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
                editTextMessage.setText("")
            } else {
                val message = ChatMessage(firebaseUser!!.uid, textMessage,
                    fromId.toString(), toID.toString(), false, 34657890)
                sendMessage(message)
                editTextMessage.setText("")
                topic = "/topics/$toID"
            }
        }

        readMessage(firebaseUser!!.uid, fromId!!)
    }

    private fun sendMessage (message: ChatMessage) {
        reference.add(message)
    }

    fun readMessage(fromId: String, toId: String) {
        //val databaseReference = Firebase.firestore.collection("PrivateMessages")
        val uid = FirebaseAuth.getInstance().uid
        reference.addSnapshotListener {value, error->
            for (document in value!!) {
                val chat = document.toObject(ChatMessage::class.java)
                if(chat.fromId == uid){
                    adapter.add(ChatFrom(chat))
                }
                else{
                    adapter.add(ChatTo(chat))
                }
                println("mensagens" + chat.text)
            }
        }
    }

class ChatFrom(val chat: ChatMessage) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textMessage = viewHolder.itemView.findViewById<TextView>(R.id.textViewChatMessage)
        textMessage.text = chat.text
    }

    override fun getLayout(): Int {
        return R.layout.row_text_message_from
    }

}
    class ChatTo(val chat: ChatMessage) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val textMessage = viewHolder.itemView.findViewById<TextView>(R.id.textViewChatMessageto)
            textMessage.text = chat.text
        }

        override fun getLayout(): Int {
            return R.layout.row_text_message_to
        }

    }

}