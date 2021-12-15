package ipca.project.ipchatv2.Chat

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.ShowUsersActivity
import ipca.project.ipchatv2.UserItem
import ipca.project.ipchatv2.databinding.ActivityCreateNewGroupBinding
import ipca.project.ipchatv2.databinding.ActivityShowUsersBinding
import java.util.*
import kotlin.collections.ArrayList
import android.text.Editable

import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.Models.PrivateChannel
import ipca.project.ipchatv2.Utils
import okhttp3.internal.Util


class CreateNewGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNewGroupBinding
    val adapter = GroupAdapter<ViewHolder>()
    private var db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerViewShowGroupUser = binding.recyclerViewShowGroupUser
        val textViewLenght = binding.textViewLenght
        val editTextGroupName = binding.editTextGroupName
        val buttonCreateGroup = binding.buttonCreateGroup


        val bundle = intent.extras
        val userIds = bundle!!.getStringArrayList("userIds")

        val length: Int = editTextGroupName.text.length
        textViewLenght.text = "$length/40"

        editTextGroupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                val length: Int = editTextGroupName.length()
                textViewLenght.text = "${length.toShort()}/40"

                if(length > 40)
                    textViewLenght.setTextColor(Color.RED)
                else if(length <= 40)
                    textViewLenght.setTextColor(Color.BLACK)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        addUsersToAdapter(userIds)

        recyclerViewShowGroupUser.adapter = adapter

        buttonCreateGroup.setOnClickListener {

            if(editTextGroupName.text.isEmpty() || editTextGroupName.text.length > 40)
                Toast.makeText(this, "Introduza um nome válido!", Toast.LENGTH_SHORT).show()
            else
            {

                userIds!!.add(currentUser.uid)
                val groupName = editTextGroupName.text.toString()
                val groupImage = "https://firebasestorage.googleapis.com/v0/b/ipchat-29feb.appspot.com/o/group.png?alt=media&token=3fdbb878-1b0c-435a-b667-c155c6aecf36"
                val groupChannel = GroupChannel(groupName, userIds, groupImage)

                    db.collection("groupChannels")
                        .add(groupChannel)
                        .addOnSuccessListener {

                            val date = Calendar.getInstance().time
                            val firstMessage = ChatMessage(null, null, date, "firstMessage")

                            val refSendMessage = db.collection("groupChannels")
                                .document(it.id)
                                .collection("messages")

                            val refSendLastMessage = db.collection("groupChannels")
                                .document(it.id)
                                .collection("lastMessage")

                            refSendMessage.add(firstMessage).addOnSuccessListener {

                                refSendLastMessage.document(it.id)
                                    .set(firstMessage)

                            }

                            userIds.forEachIndexed{ index, userId ->

                                println("userId = $userId")

                                if(userId == currentUser.uid)
                                {

                                    db.collection("User")
                                        .document(userId)
                                        .collection("groupChannels")
                                        .document(it.id)
                                        .set(mapOf("admin" to true))

                                }
                                else
                                {

                                    db.collection("User")
                                        .document(userId)
                                        .collection("groupChannels")
                                        .document(it.id)
                                        .set(mapOf("admin" to false))

                                }


                                if(index == (userIds.size - 1))
                                    startChatActivity(it.id)

                            }
                        }
            }

        }


    }

    private fun startChatActivity(channelId: String){

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("groupId", channelId)
        intent.putExtra("channelType", "group")
        startActivity(intent)
        finish()

    }

    private fun addUsersToAdapter(userIds: ArrayList<String>?) {

        userIds!!.forEach {

            println("it = $it")

            db.collection("User")
                .document(it)
                .get()
                .addOnSuccessListener { result->

                    val user = result.toObject(User::class.java)

                    adapter.add(UserItem(user!!))

                }

        }

    }
}