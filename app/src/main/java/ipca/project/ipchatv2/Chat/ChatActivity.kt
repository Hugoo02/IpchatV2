package ipca.project.ipchatv2.Chat

import android.content.*
import android.content.ContentValues.TAG
import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.Models.PrivateChannel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import ipca.project.ipchatv2.Notifications.NotificationData
import ipca.project.ipchatv2.Notifications.PushNotification
import ipca.project.ipchatv2.Notifications.RetrofitInstance
import ipca.project.ipchatv2.Calendar.CalendarActivity
import ipca.project.ipchatv2.MainActivity
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.fragment_calendar.*
import android.provider.OpenableColumns
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import ipca.project.ipchatv2.Models.*
import ipca.project.ipchatv2.Utils.Utils
import kotlinx.android.synthetic.main.row_calendar.*
import java.io.File
import java.lang.Exception
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import android.view.Gravity

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    val adapter = GroupAdapter<ViewHolder>()

    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

    var groupId: String? = null
    var channelType: String? = null

    val db = FirebaseFirestore.getInstance()

    val currentUser = FirebaseAuth.getInstance().uid

    var photoFile : File? = null
    val FILE_NAME = "photo.jpg"

    val messages: MutableList<ChatMessage> = ArrayList()
    val messageIdList: MutableList<String> = ArrayList()

    val getGalleryImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        val selectedPhotoUri = it.data?.data

        if (selectedPhotoUri != null)
            uploadImageToFirebaseStorage(selectedPhotoUri)

    }

    val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        val photoUri = Uri.fromFile(photoFile)

        if(it.resultCode == Activity.RESULT_OK)
            uploadImageToFirebaseStorage(photoUri)

    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                return@registerForActivityResult
            } else {
                Toast.makeText(this, "Precisa aceitar a permissão para poder enviar fotografias"
                ,Toast.LENGTH_SHORT).show()
            }
        }

    val getFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        var path = ""
        var fileName = ""
        var uri = it.data?.data

        try {

            val clipData = it.data?.clipData
            if (clipData == null && uri != null) {

                var cursor: Cursor? = null
                try {
                    contentResolver.query(it.data?.data!!, null, null, null, null).use {

                        cursor = it

                        if (cursor != null && cursor!!.moveToFirst()) {

                            fileName = cursor!!.getString(
                                cursor!!.getColumnIndex(
                                    OpenableColumns.DISPLAY_NAME
                                ).toInt()
                            )

                            val fileSize = cursor!!.getColumnIndexOrThrow(OpenableColumns.SIZE).toInt()
                            println("fileSize = $fileSize")

                        }

                    }
                } finally {
                    cursor?.close()

                }

                path += it.data?.data.toString()
            } else {

                for (i in 0 until clipData!!.itemCount) {
                    val item = clipData.getItemAt(i)
                    val uri: Uri = item.uri
                    path += uri.toString() + "\n"
                }
            }

            Toast.makeText(this, "path = $path", Toast.LENGTH_LONG).show()
            println("path = $path")

            val file = File("$path/$fileName")

            Toast.makeText(this, "file.name = ${file.name}", Toast.LENGTH_SHORT).show()

            uploadFileToFireStorage(uri!!, file)

        }catch (exception: Exception){}





    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewChat.adapter = adapter

        groupId = intent.getStringExtra("groupId")
        channelType = intent.getStringExtra("channelType")

        supportActionBar!!.hide()

        binding.constraintLayout.bringToFront()

        val bundle = intent.extras
        var receiverTokenShowUsers = ""
        var receiverTokenUserList = ""
        bundle?.let {
            receiverTokenShowUsers = it.getString("Token").toString()
            receiverTokenUserList = it.getString("receiverToken").toString()
        }

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

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_choose_pic_method)

            val linearLayoutCam = dialog.findViewById<LinearLayout>(R.id.linearLayoutCam)
            val linearLayoutGallery = dialog.findViewById<LinearLayout>(R.id.linearLayoutGallery)

            linearLayoutCam.setOnClickListener {

                takePicture()
                dialog.dismiss()

            }

            linearLayoutGallery.setOnClickListener {

                selectImage()
                dialog.dismiss()

            }

            dialog.show()
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setGravity(Gravity.BOTTOM)

        }

        binding.imageButtonFile.setOnClickListener {

            selectFile()

        }

        adapter.setOnItemClickListener { item, view ->

            if(binding.constraintMessage.visibility == View.INVISIBLE){

                binding.constraintMessage.visibility = View.VISIBLE

                binding.linearLayoutHoldMessage.visibility = View.INVISIBLE

            }

            if(item.layout == R.layout.row_image_message_from)
            {

                val row = item as ImageFromItem

                val intent = Intent(this, ShowPhotoCloserActivity::class.java)
                intent.putExtra("groupId", groupId)
                intent.putExtra("channelType", channelType)
                intent.putExtra("message", row.message)
                startActivity(intent)

            }else if (item.layout == R.layout.row_image_message_to){

                val row = item as ImageToItem

                val intent = Intent(this, ShowPhotoCloserActivity::class.java)
                intent.putExtra("groupId", groupId)
                intent.putExtra("channelType", channelType)
                intent.putExtra("message", row.message)
                startActivity(intent)

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

                var message : ChatMessage? = null

                if(item.layout == R.layout.row_text_message_from)
                {

                    val row = item as ChatFromItem
                    message = row.message

                }else{

                    val row = item as ImageFromItem
                    message = row.message

                }

                messages.forEachIndexed { index, chatMessage ->

                    if (message == chatMessage) {
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

                    binding.constraintMessage.visibility = View.VISIBLE

                    binding.linearLayoutHoldMessage.visibility = View.INVISIBLE

                }
            }

            return@setOnItemLongClickListener true

        }



        binding.imageButtonSendMessage.setOnClickListener {

            Log.d(TAG, "nova mensagem")

            val ref = FirebaseFirestore.getInstance().collection("User").document(currentUser!!)

            val uid = FirebaseAuth.getInstance().uid
            //val ref = Firebase.firestore.collection("User").document(uid!!)

            ref.addSnapshotListener { value, error ->

                val user = value!!.toObject(User::class.java)

                val title = user!!.username
                val message = editTextMessage.text.toString()
                var receiverToken = ""
                //val receiverTokenFromShowUsers = receiverTokenShowUsers
                //val receiverTokenFromUserList = receiverTokenUserList

                if(receiverTokenShowUsers == "null" && receiverTokenUserList != "null"){
                    receiverToken = receiverTokenUserList
                }
                else if(receiverTokenUserList == "null" && receiverTokenShowUsers != "null"){
                    receiverToken = receiverTokenShowUsers
                }

                var profileImage = ""
                profileImage = user!!.imageURL!!

                println(receiverToken)
                println(profileImage)


                if(title!!.isNotEmpty() && message.isNotEmpty()) {
                    PushNotification(
                        NotificationData(title, message, profileImage),
                        receiverToken
                    ).also {
                        sendNotification(it)
                    }
                }



            }

            performSendMessage()


            /*
            ref.get().addOnSuccessListener { value ->

                val user = value!!.toObject(User::class.java)

                profileImage = user!!.imageURL!!

            }

             */

        }


    }

    private fun selectFile(){

        val intent = Intent()
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        val extraMimeTypes = arrayOf("application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
            "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
            "text/plain",
            "application/pdf",
            "application/zip",
            "image/gif", "image/jpeg", "image/jpg", "image/png", "image/svg+xml", "image/webp", "image/vnd.wap.wbmp", "image/vnd.nok-wallpaper", "text/xml",
            "application/json",
            "text/json",
            "text/javascript"
        )
        intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes)
        getFile.launch(intent)

    }

    private fun selectImage() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getGalleryImage.launch(intent)

    }

    private fun takePicture() {

        //requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        photoFile = getPhotoFile(FILE_NAME)
        val fileProvider =
            FileProvider.getUriForFile(this, "ipca.project.ipchatv2.fileprovider", photoFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

        if(intent.resolveActivity(this.packageManager) != null) {
            takePicture.launch(intent)
        }

    }

    private fun getPhotoFile(fileName: String): File {

        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)

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


        var string : String
        var string1: String

        string = imageURL.toString()


        val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
        val tmp = factory.generateSecret(spec)
        val secretKey =  SecretKeySpec(tmp.encoded, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        string1 = Base64.encodeToString(cipher.doFinal(string.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)



        val message = ChatMessage(currentUser, string1, date, "IMAGE")

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

            refGroup.addSnapshotListener { result, error ->

                val group = result!!.toObject(GroupChannel::class.java)

                binding.textViewUsername.text = group!!.chatName

            }

        }
        else{

            val refPrivateChannel = db.collection("privateChannels")
                .document(groupId!!)

            refPrivateChannel.addSnapshotListener { result, error ->

                val users = result!!.toObject(PrivateChannel::class.java)

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

        Log.d(TAG, "sendmessage")

        val text = binding.editTextMessage.text.toString()

        var string : String
        var string1: String

        string = text.toString()


        val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
        val tmp = factory.generateSecret(spec)
        val secretKey =  SecretKeySpec(tmp.encoded, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        string1 = Base64.encodeToString(cipher.doFinal(string.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)



        val date = Calendar.getInstance().time

        val message = ChatMessage(currentUser, string1, date, "TEXT")

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

                if(channelType == "private" && index == 0){

                    adapter.add(HourItem(chatMessage.time!!))
                    messageDate = true

                }else if (channelType == "group" && index == 0){
                    messageDate = false
                } else{

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

                        println("index = $index")

                        adapter.add(HourItem(chatMessage.time!!))
                        messageDate = true

                    }
                    else
                        messageDate = false

                }


                if(index > 0 && chatMessage.senderId!! == messages[index - 1].senderId && !messageDate)
                    details = false


                if(chatMessage.text != null){
                    var string1 :String

                    val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

                    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                    val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
                    val tmp = factory.generateSecret(spec);
                    val secretKey =  SecretKeySpec(tmp.encoded, "AES")

                    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
                    string1 = String(cipher.doFinal(Base64.decode(chatMessage.text, Base64.DEFAULT)))


                    chatMessage.text = string1
                }


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

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(ContentValues.TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(ContentValues.TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(ContentValues.TAG, e.toString())
        }
    }

    private fun uploadFileToFireStorage(uri: Uri, file: File){


        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/files/${filename}")
        val extension = file.extension

        println("passou aqui")

        if(extension != "jpeg" && extension != "jpg" && extension !in "png") {
            ref.putFile(uri)
                .addOnSuccessListener {

                    println("passou ali")

                    println(file)

                    Toast.makeText(this, "Ficheiro adicionado á base de dados", Toast.LENGTH_SHORT)
                        .show()
                    ref.downloadUrl.addOnSuccessListener {

                        val name = file.name
                        val fileSize = (file.length() / (1024 * 1024)).toString()

                        println("extension = $extension")

                        val fileItem = FileModel(name, fileSize, extension)

                        db.collection("Files")
                            .add(fileItem)
                            .addOnSuccessListener {

                                performFileMessage(it.id)

                            }

                    }

                }.addOnFailureListener {
                    Toast.makeText(this, "Erro a uploadar a imagem!", Toast.LENGTH_SHORT).show()
                }
        }else{

            uploadImageToFirebaseStorage(uri)

        }

    }

    private fun performFileMessage(fileDocument: String) {

        val date = Calendar.getInstance().time

        var string : String
        var string1: String

        string = fileDocument.toString()


        val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
        val tmp = factory.generateSecret(spec)
        val secretKey =  SecretKeySpec(tmp.encoded, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        string1 = Base64.encodeToString(cipher.doFinal(string.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)

        val message = ChatMessage(currentUser, string1, date, "FILE")

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

            refSendLastMessage.document(it.id).set(message)

        }

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var path = ""
        var fileName = ""
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                val clipData = data?.clipData
                if (clipData == null) {

                    var cursor: Cursor? = null
                    try {
                        contentResolver.query(data?.data!!, null, null, null, null).use {

                            cursor = it

                            if (cursor != null && cursor!!.moveToFirst()) {

                                fileName = cursor!!.getString(cursor!!.getColumnIndex(
                                    OpenableColumns.DISPLAY_NAME).toInt())

                            }

                        }
                    } finally {
                        cursor?.close()

                    }

                    path += data?.data.toString()
                }
                else {

                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        val uri: Uri = item.uri
                        path += uri.toString() + "\n"
                    }
                }

            }
        }

        Toast.makeText(this, "path = $path", Toast.LENGTH_LONG).show()
        println("path = $path")

        val file = File("$path/$fileName")

        Toast.makeText(this, "file.name = ${file.name}", Toast.LENGTH_SHORT).show()

        val stream = FileInputStream(file)

        val ref = FirebaseStorage.getInstance().getReference("/files/$fileName")

        ref.putStream(stream)
            .addOnSuccessListener {

                Toast.makeText(this, "Ficheiro adicionado á base de dados", Toast.LENGTH_SHORT).show()
                ref.downloadUrl.addOnSuccessListener {

                    db.collection("Files")
                        .add(file)
                        .addOnSuccessListener {

                            performFileMessage(it.id)

                        }

                }

            }
            .addOnFailureListener{
                Toast.makeText(this, "Erro a uploadar a imagem!", Toast.LENGTH_SHORT).show()
            }

        // Upload and refresh list (aqui utiliza da forma que fazes)
        //Utilis.uploadFile(path.toUri(), "$currentPath/$fileName")
        //files.add(FirebaseFile(fileName, Utilis.getFileIcon(fileName)))
        //filesAdapter?.notifyDataSetChanged()
    }*/
}