package ipca.project.ipchatv2.Home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import ipca.project.ipchatv2.Chat.ChatActivity
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.UserItem
import ipca.project.ipchatv2.databinding.FragmentHomeBinding
import ipca.project.ipchatv2.databinding.FragmentUserListBinding
import kotlinx.android.synthetic.main.row_users.view.*

class UserListFragment : Fragment() {
    private lateinit var binding: FragmentUserListBinding
    private var db = FirebaseFirestore.getInstance()
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "IPCHAT"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.inflate(layoutInflater)

        fetchUsers()

        binding.recyclerViewShowUsers.adapter = adapter

        adapter.setOnItemClickListener { item, view ->

            var userItem = item as UserItem

            val intent = Intent(view.context, ChatActivity::class.java)
            intent.putExtra("User", userItem.user)
            //intent.flags()
            startActivity(intent)

        }

        return binding.root
    }

    private fun fetchUsers(){

        //val adapter = GroupieAdapter()

        db.collection("User")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = User.fromHash(document.data as HashMap<String, Any?>)
                    adapter.add(UserItem(user))
                }
            }
            .addOnFailureListener { exception ->
                println("Erro")
            }
    }

}

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

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