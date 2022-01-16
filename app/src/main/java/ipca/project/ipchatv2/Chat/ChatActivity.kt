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
import ipca.project.ipchatv2.Calendar.CalendarActivity
import ipca.project.ipchatv2.MainActivity
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import kotlinx.android.synthetic.main.fragment_calendar.*
import android.os.Environment
import android.util.Log
import android.view.View
import ipca.project.ipchatv2.Files.InsidePathActivity
import ipca.project.ipchatv2.Files.SelectPathActivity
import ipca.project.ipchatv2.Utils.Utils
import kotlinx.android.synthetic.main.row_calendar.*
import java.io.File


class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    val adapter = GroupAdapter<ViewHolder>()

    var groupId : String? = null
    var channelType: String? = null

    val db = FirebaseFirestore.getInstance()

    val currentUser = FirebaseAuth.getInstance().uid

    val messages : MutableList<ChatMessage> = ArrayList()
    val messageIdList : MutableList<String> = ArrayList()

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

        binding.imageButtonCalendar.setOnClickListener {

            val intent = Intent(this, CalendarActivity::class.java)
            intent.putExtra("calendarId", groupId)
            intent.putExtra("channelType", channelType)
            startActivity(intent)

        }

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

        binding.imageButtonFile.setOnClickListener {

            val path = Environment.getExternalStorageDirectory().absolutePath.toString()

            println("path = $path")

            val intent = Intent(this, InsidePathActivity::class.java)
            intent.putExtra("requestPath", path)
            intent.putExtra("channelType", channelType)
            intent.putExtra("groupId", groupId)
            startActivity(intent)

            listenForMessages()
            selectFile()

        }

        adapter.setOnItemClickListener { item, view ->

            if(binding.constraintMessage.visibility == View.INVISIBLE){

                binding.constraintMessage.visibility = View.VISIBLE

                binding.linearLayoutHoldMessage.visibility = View.INVISIBLE

            }

        }

        adapter.setOnItemLongClickListener { item, view ->

            if(item.layout == R.layout.row_removed_message_to || item.layout == R.layout.row_removed_message_from)
            {

                binding.constraintMessage.visibility = View.VISIBLE

                binding.linearLayoutHoldMessage.visibility = View.INVISIBLE

            }else{

                binding.constraintMessage.visibility = View.INVISIBLE

                binding.linearLayoutHoldMessage.visibility = View.VISIBLE

            }

            if(item.layout == R.layout.row_text_message_to)
                binding.linearLayoutRemove.visibility = View.GONE
            else
                binding.linearLayoutRemove.visibility = View.VISIBLE

            binding.linearLayoutCopy.setOnClickListener {

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

                Toast.makeText(this, "Copiado", Toast.LENGTH_LONG).show()

                binding.constraintMessage.visibility = View.VISIBLE

                binding.linearLayoutHoldMessage.visibility = View.INVISIBLE

            }

            binding.linearLayoutRemove.setOnClickListener {

                val row = item as ChatFromItem

                messages.forEachIndexed { index, chatMessage ->

                    if (row.message == chatMessage) {
                        if (channelType == "group") {

                            if (index == (messages.size - 1)) {

                                db.collection("groupChannels")
                                    .document(groupId!!)
                                    .collection("lastMessage")
                                    .document(chatMessage.messageId!!)
                                    .update(mapOf("type" to "REMOVED"))

                            }

                            db.collection("groupChannels")
                                .document(groupId!!)
                                .collection("messages")
                                .document(chatMessage.messageId!!)
                                .update(mapOf("type" to "REMOVED"))

                        } else {

                            if (index == (messages.size - 1)) {

                                db.collection("privateChannels")
                                    .document(groupId!!)
                                    .collection("lastMessage")
                                    .document(chatMessage.messageId!!)
                                    .update(mapOf("type" to "REMOVED"))

                            }

                            db.collection("privateChannels")
                                .document(groupId!!)
                                .collection("messages")
                                .document(chatMessage.messageId!!)
                                .update(mapOf("type" to "REMOVED"))

                        }
                    }

                    adapter.notifyDataSetChanged()

                    binding.constraintMessage.visibility = View.VISIBLE

                    binding.linearLayoutHoldMessage.visibility = View.INVISIBLE

                }
            }

            return@setOnItemLongClickListener true

        }

        binding.imageButtonSendMessage.setOnClickListener {

            performSendMessage()

        }
    }

    private fun selectFile() {

        val path = Environment.getExternalStorageDirectory().toString()
        Log.d("Files", "Path: $path")
        val directory = File(path)
        val files: Array<File> = directory.listFiles()
        Log.d("Files", "Size: " + files.size)
        for (i in files.indices) {
            Log.d("Files", "FileName:" + files[i].getName())
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

                refSendMessage.document(it.id).update(mapOf("messageId" to it.id))

                message.messageId = it.id

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
            messageIdList.clear()
            adapter.clear()

            for (doc in result!!){

                messages.add(doc.toObject(ChatMessage::class.java))
                messageIdList.add(doc.id)

            }

            messages.sortBy{it.time}

            var messageDate = false

            messages.forEachIndexed { index, chatMessage ->

                var details = true

                if(index == 0){

                    adapter.add(HourItem(chatMessage.time!!))
                    messageDate = true

                }else{

                    val previousMessageDate = Utils.dateToCalenar(messages[index - 1].time!!)
                    val currentMessageDate = Utils.dateToCalenar(chatMessage.time!!)

                    val previousMessageYear = previousMessageDate.get(Calendar.YEAR)
                    val previousMessageMonth = previousMessageDate.get(Calendar.MONTH)
                    val previousMessageDay = previousMessageDate.get(Calendar.DAY_OF_MONTH)
                    val previousMessageHour = previousMessageDate.get(Calendar.HOUR)
                    val previousMessageMinutes = previousMessageDate.get(Calendar.MINUTE)

                    val currentMessageYear = currentMessageDate.get(Calendar.YEAR)
                    val currentMessageMonth = currentMessageDate.get(Calendar.MONTH)
                    val currentMessageDay = currentMessageDate.get(Calendar.DAY_OF_MONTH)
                    val currentMessageHour = currentMessageDate.get(Calendar.HOUR)
                    val currentMessageMinutes = currentMessageDate.get(Calendar.MINUTE)

                    if(!(previousMessageYear == currentMessageYear && previousMessageMonth == currentMessageMonth
                        && previousMessageDay == currentMessageDay && previousMessageHour == currentMessageHour
                        && previousMessageMinutes >= (currentMessageMinutes - 5)))
                    {

                        adapter.add(HourItem(chatMessage.time!!))
                        messageDate = true

                    }
                    else
                        messageDate = false

                }

                println("messageDate = $messageDate")

                if(index > 0 && chatMessage.senderId!! == messages[index - 1].senderId && !messageDate)
                    details = false

                println("details = $details")

                if(chatMessage.type == "firstMessage")
                    adapter.add(FirstMessage(chatMessage))
                else if (chatMessage.senderId == currentUser && chatMessage.type == "TEXT")
                    adapter.add(ChatFromItem(chatMessage))
                else if (chatMessage.type == "TEXT")
                    adapter.add(ChatToItem(chatMessage, details))
                else if (chatMessage.senderId == currentUser && chatMessage.type == "IMAGE")
                    adapter.add(ImageFromItem(chatMessage))
                else if ((chatMessage.senderId != currentUser && chatMessage.type == "IMAGE"))
                    adapter.add(ImageToItem(chatMessage))
                else if (chatMessage.senderId == currentUser && chatMessage.type == "REMOVED")
                    adapter.add(ChatFromRemoved(chatMessage))
                else if (chatMessage.senderId != currentUser && chatMessage.type == "REMOVED")
                    adapter.add(ChatToRemoved(chatMessage, details))
                else if(chatMessage.senderId == currentUser && chatMessage.type == "FILE")
                    adapter.add(FileItemFrom(chatMessage))
                else
                    adapter.add(FileItemTo(chatMessage, details))


            }

            recyclerViewChat.scrollToPosition(adapter.itemCount - 1)

        }

    }
}