package ipca.project.ipchatv2.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import ipca.project.ipchatv2.databinding.ActivityChatMoreDetailsBinding
import ipca.project.ipchatv2.databinding.ActivityCreateNewGroupBinding

class ChatMoreDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatMoreDetailsBinding

    var groupId : String? = null
    var channelType: String? = null

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMoreDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        groupId = intent.getStringExtra("groupId")
        channelType = intent.getStringExtra("channelType")

        configureActivity()

        binding.buttonBack.setOnClickListener {

            finish()

        }

        binding.textViewGroupMembers.setOnClickListener {

            val intent = Intent(this, GroupMembersActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)

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

        }

    }
}