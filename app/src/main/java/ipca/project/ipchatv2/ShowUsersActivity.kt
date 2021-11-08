package ipca.project.ipchatv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.ActivityShowUsersBinding

class ShowUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowUsersBinding
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Select User"

        fetchUsers()
    }

    private fun fetchUsers(){

        val adapter = GroupieAdapter()
        //val adapter = GroupAdapter<ViewHolder>()
        binding.recyclerViewShowUsers.adapter = adapter


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

class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        //val textViewUserName = viewHolder.textViewUserName
        //val circleImagePhoto = viewHolder.itemView.circleImageView

        //textViewUserName.text = user.username


        //Glide.with(ShowUsersActivity()).load(user.imageURL).override(100, 100).centerCrop().into(circleImagePhoto)
    }

    override fun getLayout(): Int {
        return R.layout.row_users
    }
}