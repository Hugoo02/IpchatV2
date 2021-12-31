package ipca.project.ipchatv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Chat.ChatActivity
import ipca.project.ipchatv2.Chat.CreateNewGroupActivity
import ipca.project.ipchatv2.Models.PrivateChannel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.RowConfigurations.UserItem
import ipca.project.ipchatv2.databinding.ActivityShowUsersBinding
import kotlinx.android.synthetic.main.row_users.view.*
import android.app.Activity




class ShowUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowUsersBinding
    private var db = FirebaseFirestore.getInstance()
    val adapter = GroupAdapter<ViewHolder>()
    val currentUser = FirebaseAuth.getInstance()
    val userIds : ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val channelType = intent.getStringExtra("channelType")

        binding.buttonBack.setOnClickListener {

            finish()

        }

        fetchUsers()

        binding.recyclerViewShowUsers.adapter = adapter

        if(channelType == "private")
        {

            adapter.setOnItemClickListener { item, view ->

                val userItem = item as UserItem
                var guestUserId : String? = null
                var channelId : String? = null

                db.collection("User")
                    .document(userItem.user.id!!)
                    .get()
                    .addOnSuccessListener { result ->

                        guestUserId = result.toObject(User::class.java)!!.id

                        println("guestUserId = $guestUserId")

                        db.collection("User")
                            .document(currentUser.uid!!)
                            .collection("privateChannel")
                            .document(guestUserId!!)
                            .get().addOnSuccessListener {

                                if(it.exists()){

                                    channelId = it["channelId"] as String
                                    startChatActivity(channelId!!)

                                }
                                else
                                {

                                    db.collection("privateChannels")
                                        .add(PrivateChannel(mutableListOf(currentUser.uid!!, guestUserId!!)))
                                        .addOnSuccessListener {

                                            db.collection("User")
                                                .document(currentUser.uid!!)
                                                .collection("privateChannel")
                                                .document(guestUserId!!)
                                                .set(mapOf("channelId" to it.id))

                                            db.collection("User")
                                                .document(guestUserId!!)
                                                .collection("privateChannel")
                                                .document(currentUser.uid!!)
                                                .set(mapOf("channelId" to it.id))

                                            channelId = it.id
                                            startChatActivity(channelId!!)

                                    }
                                }
                        }
                    }


            }

        }
        else
        {

            binding.imageButtonNext.setOnClickListener {

                if(userIds.size < 2)
                    Toast.makeText(this, "O grupo tem que ter pelo menos 3 membros!", Toast.LENGTH_SHORT).show()
                else
                {

                    val bundle = Bundle()
                    bundle.putStringArrayList("userIds", userIds)

                    val intent = Intent(this, CreateNewGroupActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)

                }

            }

            adapter.setOnItemClickListener { item, view ->

                val row = item as UserItem

                view.isSelected = !view.isSelected

                if(view.isSelected){

                    userIds.add(row.user.id!!)
                    if(userIds.size >= 2)
                        binding.imageButtonNext.visibility = View.VISIBLE

                }
                else{

                    userIds.remove(row.user.id)
                    if(userIds.size < 2)
                        binding.imageButtonNext.visibility = View.INVISIBLE

                }

                adapter.notifyDataSetChanged()

            }
        }

    }

    private fun startChatActivity(channelId: String){

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("groupId", channelId)
        intent.putExtra("channelType", "private")
        startActivity(intent)
        finish()

    }

    private fun fetchUsers(){

        db.collection("User")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(User::class.java)

                    if(user.id != currentUser.uid)
                    {

                        adapter.add(UserItem(user, false))

                    }

                }
            }
            .addOnFailureListener { exception ->
                println("Erro")
            }
    }

    fun closeActivity(){

        finish()

    }

}

