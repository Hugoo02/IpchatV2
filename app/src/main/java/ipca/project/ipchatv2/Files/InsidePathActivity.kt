package ipca.project.ipchatv2.Files

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.FileModel
import ipca.project.ipchatv2.RowConfigurations.FileItem
import ipca.project.ipchatv2.databinding.ActivityInsidePathBinding
import ipca.project.ipchatv2.databinding.ActivitySelectPathBinding
import java.io.File
import java.io.FileInputStream
import java.util.*

class InsidePathActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInsidePathBinding
    val adapter = GroupAdapter<ViewHolder>()

    val currentUser = FirebaseAuth.getInstance().uid

    val db = FirebaseFirestore.getInstance()

    var path : String? = null
    var channelType : String? = null
    var groupId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsidePathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        path = intent.getStringExtra("requestPath")
        channelType = intent.getStringExtra("channelType")
        groupId = intent.getStringExtra("groupId")

        refreshAdapter(getFileModelsFromFiles(getFilesFromPath(path!!)))

        binding.recyclerViewFiles.adapter = adapter

        adapter.setOnItemClickListener { item, view ->

            val row = item as FileItem

            if(row.file.type == "FOLDER")
            {

                path += "/${row.file.name}"
                val files = getFileModelsFromFiles(getFilesFromPath(path!!))

                refreshAdapter(files)

            }else
            {

                val filename = UUID.randomUUID().toString()
                val ref = FirebaseStorage.getInstance().getReference("/files/$filename")

                val stream = FileInputStream(File(row.file.path))

                ref.putStream(stream)
                    .addOnSuccessListener {

                        Toast.makeText(this, "Ficheiro adicionado á base de dados", Toast.LENGTH_SHORT).show()
                        ref.downloadUrl.addOnSuccessListener {

                            db.collection("Files")
                                .add(row.file)
                                .addOnSuccessListener {

                                    performSendMessage(it.id)

                                }

                        }

                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Erro a uploadar a imagem!", Toast.LENGTH_SHORT).show()
                    }

            }
        }

    }

    private fun refreshAdapter(files: List<FileModel>) {

        adapter.clear()

        files.forEach{

            adapter.add(FileItem(it))

        }

    }

    private fun getFiles() {

        adapter.clear()

        println("path = $path")

        val files: Array<File> = File(path).listFiles()

        for (file in files) {

            val fileSize = file.length()

            val sizeInMb = fileSize / (1024.0 * 1024)

            var type : String? = null

            if(file.isDirectory)
                type = "FOLDER"
            else
                type = "FILE"

            val pathFile = "$path/${file.name}"
            Toast.makeText(this, pathFile, Toast.LENGTH_SHORT).show()

            adapter.add(FileItem(FileModel(file.name, sizeInMb, pathFile, type)))
        }

    }

    private fun performSendMessage(fileDocument: String) {

        val date = Calendar.getInstance().time

        val message = ChatMessage(currentUser, fileDocument, date, "FILE")

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

            finish()

        }

    }
}