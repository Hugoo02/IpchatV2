package ipca.project.ipchatv2.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.User
import java.util.*
import kotlin.collections.ArrayList
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ipca.project.ipchatv2.CustomDialogs.ChooseImageFragment
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.RowConfigurations.UserItem
import ipca.project.ipchatv2.databinding.ActivityCreateNewGroupBinding
import android.content.DialogInterface





class CreateNewGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNewGroupBinding
    val adapter = GroupAdapter<ViewHolder>()
    private var db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateNewGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        val recyclerViewShowGroupUser = binding.recyclerViewShowGroupUser
        val editTextGroupName = binding.editTextGroupName
        val imageButtonFinish = binding.imageButtonFinish

        val bundle = intent.extras
        val userIds = bundle!!.getStringArrayList("userIds")

        binding.buttonBack.setOnClickListener {

            finish()

        }

        addUsersToAdapter(userIds)

        recyclerViewShowGroupUser.adapter = adapter

        imageButtonFinish.setOnClickListener {

            if(editTextGroupName.text.isEmpty() || editTextGroupName.text.length > 40)
                Toast.makeText(this, "Introduza um nome válido!", Toast.LENGTH_SHORT).show()
            else
            {

                userIds!!.add(currentUser.uid)
                val groupName = editTextGroupName.text.toString()
                val groupChannel = GroupChannel(groupName, userIds, null)

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
                                {

                                    chooseGroupImage(it.id)

                                }


                            }
                        }
            }

        }


    }

    private fun chooseGroupImage(groupId: String) {

        val bundle = Bundle()
        bundle.putString("groupId", groupId)
        val dialog = ChooseImageFragment()
        dialog.arguments = bundle

        dialog.show(supportFragmentManager, "customDialog")

    }

    private fun addUsersToAdapter(userIds: ArrayList<String>?) {

        userIds!!.forEach {

            println("it = $it")

            db.collection("User")
                .document(it)
                .get()
                .addOnSuccessListener { result->

                    val user = result.toObject(User::class.java)

                    adapter.add(UserItem(user!!, false))

                }

        }

    }
}