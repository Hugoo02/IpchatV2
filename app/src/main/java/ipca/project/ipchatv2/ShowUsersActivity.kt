package ipca.project.ipchatv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Chat.ChatActivity
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.ActivityShowUsersBinding
import kotlinx.android.synthetic.main.row_users.view.*

class ShowUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowUsersBinding
    private var db = FirebaseFirestore.getInstance()
    val adapter = GroupAdapter<ViewHolder>()
    val currentUser = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Select User"

        fetchUsers()

        binding.recyclerViewShowUsers.adapter = adapter

        adapter.setOnItemClickListener { item, view ->

            var userItem = item as UserItem

            val intent = Intent(view.context, ChatActivity::class.java)
            intent.putExtra("id", userItem.user.id)
            startActivity(intent)
            finish()

        }
    }

    private fun fetchUsers(){

        db.collection("User")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(User::class.java)

                    if(user.id != currentUser.uid)
                        adapter.add(UserItem(user))
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


        Picasso.get().load(user.imageURL).resize(100, 100).centerCrop()
            .into(circleImagePhoto)
    }

    override fun getLayout(): Int {
        return R.layout.row_users
    }
}