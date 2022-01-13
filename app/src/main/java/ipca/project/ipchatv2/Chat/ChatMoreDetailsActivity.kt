package ipca.project.ipchatv2.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.Models.PrivateChannel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import ipca.project.ipchatv2.databinding.ActivityChatMoreDetailsBinding
import ipca.project.ipchatv2.databinding.ActivityCreateNewGroupBinding

class ChatMoreDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatMoreDetailsBinding
    val currentUserId = FirebaseAuth.getInstance().uid

    var groupId : String? = null
    var channelType: String? = null
    var admin : Boolean? = null

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMoreDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        groupId = intent.getStringExtra("groupId")
        channelType = intent.getStringExtra("channelType")

        checkIfAdmin()

        configureActivity()

        binding.buttonBack.setOnClickListener {

            finish()

        }

        binding.textViewGroupMembers.setOnClickListener {

            val intent = Intent(this, GroupMembersActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("admin", admin)
            startActivity(intent)

        }

        binding.textViewFiles.setOnClickListener {

            val intent = Intent(this, ShowFilesActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("channelType", channelType)
            startActivity(intent)

        }

    }

    private fun checkIfAdmin() {

        db.collection("User")
            .document(currentUserId!!)
            .collection("groupChannels")
            .document(groupId!!)
            .get()
            .addOnSuccessListener {

                admin = it.getBoolean("admin")

            }

    }

    private fun configureActivity() {

        if(channelType == "group")
        {

            db.collection("groupChannels")
                .document(groupId!!)
                .get().addOnSuccessListener { result ->

                    val group = result.toObject(GroupChannel::class.java)

                    val circleImageViewGroup = binding.circleImageViewGroup
                    val textViewGroupName = binding.textViewGroupName

                    textViewGroupName.text = group!!.chatName

                    Picasso.get().load(group.groupImageURL).into(circleImageViewGroup)

                }

        }else{

            db.collection("privateChannels")
                .document(groupId!!)
                .get().addOnSuccessListener { result ->

                    val chat = result.toObject(PrivateChannel::class.java)

                    val circleImageViewGroup = binding.circleImageViewGroup
                    val textViewGroupName = binding.textViewGroupName

                    var guestId : String? = null

                    chat!!.userIds!!.forEach {

                        if(it != currentUserId)
                            guestId = it

                    }

                    db.collection("User")
                        .document(guestId!!)
                        .get()
                        .addOnSuccessListener { userObject ->

                            val user = userObject.toObject(User::class.java)

                            textViewGroupName.text = user!!.username

                            Picasso.get().load(user.imageURL).into(circleImageViewGroup)

                        }

                }

        }

    }
}