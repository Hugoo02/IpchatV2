package ipca.project.ipchatv2.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.Models.PrivateChannel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import com.google.firebase.storage.FirebaseStorage
import ipca.project.ipchatv2.MainActivity
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import kotlinx.coroutines.MainScope

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    val adapter = GroupAdapter<ViewHolder>()

    var groupId : String? = null
    var channelType: String? = null

    val db = FirebaseFirestore.getInstance()

    val currentUser = FirebaseAuth.getInstance().uid

    val messages : MutableList<ChatMessage> = ArrayList()

    val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        val selectedPhotoUri = it.data?.data

        uploadImageToFirebaseStorage(selectedPhotoUri!!)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewChat.adapter = adapter
        binding.recyclerViewChat.addItemDecoration(DividerItemDecoration(this, 0))

        groupId = intent.getStringExtra("groupId")
        channelType = intent.getStringExtra("channelType")

        supportActionBar!!.hide()

        binding.constraintLayout.bringToFront()

        configureToolbar()
        listenForMessages()

        binding.imageButtonDetails.setOnClickListener {

            val intent = Intent(this, ChatMoreDetailsActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("channelType", channelType)
            startActivity(intent)

        }

        binding.buttonBack.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        binding.imageButtonCamera.setOnClickListener {

            selectImage()

        }

        adapter.setOnItemLongClickListener { item, view ->

            var text: String? = null

            if(item.layout == R.layout.row_text_message_to){

                val row = item as ChatToItem
                text = row.message.text

            } else{

                val row = item as ChatFromItem
                text = row.message.text

            }

            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", text)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_LONG).show()

            return@setOnItemLongClickListener true

        }

        binding.imageButtonSendMessage.setOnClickListener {

            performSendMessage()

        }
    }

    private fun selectImage() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getImage.launch(intent)

    }

    private fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri){

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri)
            .addOnSuccessListener {

                println("Image uploaded successfully: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {

                    sendImageMessage(it.toString())

                }

            }
            .addOnFailureListener{
                Toast.makeText(this, "Erro a uploadar a imagem!", Toast.LENGTH_SHORT).show()
            }

    }

    private fun sendImageMessage(imageURL: String) {

        val date = Calendar.getInstance().time

        val message = ChatMessage(currentUser, imageURL, date, "IMAGE")

        var refSendMessage : CollectionReference? = null
        var refSendLastMessage : CollectionReference? = null

        if(channelType == "group"){

            refSendMessage = db.collection("groupChannels")
                .document(groupId!!)
                .collection("messages")

            refSendLastMessage = db.collection("groupChannels")
                .document(groupId!!)
                .collection("lastMessage")

        }else{

            refSendMessage = db.collection("privateChannels")
                .document(groupId!!)
                .collection("messages")

            refSendLastMessage = db.collection("privateChannels")
                .document(groupId!!)
                .collection("lastMessage")

        }

        refSendLastMessage.get().addOnSuccessListener {

            for(doc in it){

                refSendLastMessage.document(doc.id).delete()

            }

        }

        refSendMessage.add(message).addOnSuccessListener {

            binding.editTextMessage.text.clear()
            binding.recyclerViewChat.scrollToPosition(adapter.itemCount - 1)

            refSendLastMessage.document(it.id).set(message)

        }

    }

    private fun configureToolbar() {

        if(channelType == "group")
        {

            val refGroup = db.collection("groupChannels")
                .document(groupId!!)

            refGroup.get().addOnSuccessListener { result ->

                val group = result.toObject(GroupChannel::class.java)

                binding.textViewUsername.text = group!!.chatName

            }

        }
        else{

            val refPrivateChannel = db.collection("privateChannels")
                .document(groupId!!)

            refPrivateChannel.get().addOnSuccessListener { result ->

                val users = result.toObject(PrivateChannel::class.java)

                users!!.userIds!!.forEach {

                    if(it != currentUser){

                        val refToUser = db.collection("User")
                            .document(it)

                        refToUser.get().addOnSuccessListener { result ->

                            val user = result.toObject(User::class.java)

                            binding.textViewUsername.text = user!!.username

                        }

                    }

                }

            }

        }

    }

    private fun performSendMessage() {

        val text = binding.editTextMessage.text.toString()
        val date = Calendar.getInstance().time

        val message = ChatMessage(currentUser, text, date, "TEXT")

        var refSendMessage : CollectionReference? = null
        var refSendLastMessage : CollectionReference? = null

        if(channelType == "group"){

            refSendMessage = db.collection("groupChannels")
                .document(groupId!!)
                .collection("messages")

            refSendLastMessage = db.collection("groupChannels")
                .document(groupId!!)
                .collection("lastMessage")

        }else{

            refSendMessage = db.collection("privateChannels")
                .document(groupId!!)
                .collection("messages")

            refSendLastMessage = db.collection("privateChannels")
                .document(groupId!!)
                .collection("lastMessage")

        }

            refSendLastMessage.get().addOnSuccessListener {

                for(doc in it){

                    refSendLastMessage.document(doc.id).delete()

                }

            }

            refSendMessage.add(message).addOnSuccessListener {

                binding.editTextMessage.text.clear()
                binding.recyclerViewChat.scrollToPosition(adapter.itemCount - 1)

                refSendLastMessage.document(it.id).set(message)

            }

    }

    private fun listenForMessages() {

        var refMessages : CollectionReference? = null

        if(channelType == "group") {

            refMessages = db.collection("groupChannels")
                .document(groupId!!)
                .collection("messages")
        }
        else{

            println("groupId = $groupId")

            refMessages = db.collection("privateChannels")
                .document(groupId!!)
                .collection("messages")

        }

        refMessages.addSnapshotListener{result, e ->

            messages.clear()
            adapter.clear()

            for (doc in result!!){

                messages.add(doc.toObject(ChatMessage::class.java))

            }

            messages.sortBy{it.time}

            messages.forEach {

                if(it.type == "firstMessage")
                    adapter.add(FirstMessage(it))
                else if (it.senderId == currentUser && it.type == "TEXT")
                    adapter.add(ChatFromItem(it))
                else if (it.type == "TEXT")
                    adapter.add(ChatToItem(it))
                else if (it.senderId == currentUser && it.type == "IMAGE")
                    adapter.add(ImageFromItem(it))
                else
                    adapter.add(ImageToItem(it))

            }

            recyclerViewChat.scrollToPosition(adapter.itemCount - 1)

        }

    }
}