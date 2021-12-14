package ipca.project.ipchatv2

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.ColorOrBuilder
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Chat.ChatActivity
import ipca.project.ipchatv2.Chat.CreateNewGroupActivity
import ipca.project.ipchatv2.Chat.UserListLMRow
import ipca.project.ipchatv2.Models.PrivateChannel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.ActivityShowUsersBinding
import kotlinx.android.synthetic.main.activity_show_users.*
import kotlinx.android.synthetic.main.row_users.view.*
import kotlinx.coroutines.channels.Channel
import okhttp3.internal.Util

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

        supportActionBar?.title = "Novo Chat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val channelType = intent.getStringExtra("channelType")

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

            binding.button.visibility = View.VISIBLE

            binding.button.setOnClickListener {

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

                }
                else{

                    userIds.remove(row.user.id)

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

                        adapter.add(UserItem(user))

                    }

                }
            }
            .addOnFailureListener { exception ->
                println("Erro")
            }
    }

}

class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewUserName = viewHolder.itemView.textViewUserName
        val circleImagePhoto = viewHolder.itemView.circleImagePhoto

        textViewUserName.text = user.username


        if(viewHolder.itemView.isSelected)
        {

            viewHolder.itemView.circleImagePhoto.setImageResource(R.drawable.selected)

        }
        else
        {

            Picasso.get().load(user.imageURL).resize(100, 100).centerCrop().into(circleImagePhoto)

        }


    }

    override fun getLayout(): Int {
        return R.layout.row_users
    }
}