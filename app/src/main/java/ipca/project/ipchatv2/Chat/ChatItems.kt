package ipca.project.ipchatv2.Chat

import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.FileModel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.Utils.Utils
import kotlinx.android.synthetic.main.row_file_from.view.*
import kotlinx.android.synthetic.main.row_file_to.view.*
import kotlinx.android.synthetic.main.row_first_message.view.*
import kotlinx.android.synthetic.main.row_first_message.view.textViewHour
import kotlinx.android.synthetic.main.row_image_message_from.view.*
import kotlinx.android.synthetic.main.row_image_message_to.view.*
import kotlinx.android.synthetic.main.row_text_message_from.view.*
import kotlinx.android.synthetic.main.row_text_message_to.view.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class ChatFromItem(val message: ChatMessage): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewChatMessageFrom = viewHolder.itemView.textViewChatMessageFrom

        textViewChatMessageFrom.text = message.text

    }

    override fun getLayout(): Int {
        return R.layout.row_text_message_from
    }
}

class ChatToItem(val message: ChatMessage, val details: Boolean): Item<ViewHolder>(){

    val db = FirebaseFirestore.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewChatMessageTo = viewHolder.itemView.textViewChatMessageTo
        val imageViewPhotoTo = viewHolder.itemView.imageViewPhotoTo
        val textViewName = viewHolder.itemView.textViewName

        val refUser = db.collection("User")
            .document(message.senderId!!)

        refUser.get().addOnSuccessListener { result ->
            val user = result.toObject(User::class.java)

            textViewChatMessageTo.text = message.text

            if(!details){

                textViewName.visibility = View.INVISIBLE
                imageViewPhotoTo.visibility = View.INVISIBLE

            }else{

                textViewName.text = user!!.username

                Picasso.get().load(user.imageURL).into(imageViewPhotoTo)

            }

        }
    }

    override fun getLayout(): Int {
        return R.layout.row_text_message_to
    }
}

class ImageFromItem(val message: ChatMessage): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val imageViewFrom = viewHolder.itemView.imageViewFrom
        val progressBarImageFrom = viewHolder.itemView.progressBarImageFrom

        progressBarImageFrom.visibility = View.VISIBLE

        Picasso.get().load(message.text).into(imageViewFrom, object : Callback {
            override fun onSuccess() {

                progressBarImageFrom.visibility = View.GONE

            }
            override fun onError(e: Exception?) {

            }
        })

    }

    override fun getLayout(): Int {
        return R.layout.row_image_message_from
    }
}

class ImageToItem(val message: ChatMessage): Item<ViewHolder>(){

    val db = FirebaseFirestore.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewNameImageTo = viewHolder.itemView.textViewNameImageTo
        val imageViewContactPhotoTo = viewHolder.itemView.imageViewContactPhotoTo
        val imageViewMessageTo = viewHolder.itemView.imageViewMessageTo
        val progressBarImageTo = viewHolder.itemView.progressBarImageTo

        progressBarImageTo.visibility = View.VISIBLE

        Picasso.get().load(message.text).into(imageViewMessageTo, object : Callback {
            override fun onSuccess() {

                progressBarImageTo.visibility = View.GONE

            }
            override fun onError(e: Exception?) {

            }
        })

        val refUser = db.collection("User")
            .document(message.senderId!!)

        refUser.get().addOnSuccessListener { result ->
            val user = result.toObject(User::class.java)

            textViewNameImageTo.text = user!!.username
            Picasso.get().load(user.imageURL).into(imageViewContactPhotoTo)

        }
    }

    override fun getLayout(): Int {
        return R.layout.row_image_message_to
    }
}

class FirstMessage(val message: ChatMessage): Item<ViewHolder>(){

    val currentUserId = FirebaseAuth.getInstance().uid
    val db = FirebaseFirestore.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewFirstMessageHour = viewHolder.itemView.textViewHour
        val textViewFirstMessage = viewHolder.itemView.textViewFirstMessage

        if(message.senderId == currentUserId)
            textViewFirstMessage.text = "Tu criaste o grupo"
        else {

            db.collection("User")
                .document(message.senderId!!)
                .get()
                .addOnSuccessListener { result ->

                    val user = result.toObject(User::class.java)

                    textViewFirstMessage.text = "${user!!.username} criou o grupo"

                }
        }

        textViewFirstMessageHour.text = Utils.getMessageDate(message.time!!)

    }

    override fun getLayout(): Int {
        return R.layout.row_first_message
    }
}

class ChatFromRemoved(val message: ChatMessage): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewChatMessageFrom = viewHolder.itemView.textViewChatMessageFrom

        textViewChatMessageFrom.text = "Você removeu esta mensagem"

    }

    override fun getLayout(): Int {
        return R.layout.row_removed_message_from
    }
}

class ChatToRemoved(val message: ChatMessage, val details: Boolean): Item<ViewHolder>(){

    val db = FirebaseFirestore.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewChatMessageTo = viewHolder.itemView.textViewChatMessageTo
        val textViewName = viewHolder.itemView.textViewName
        val imageViewPhotoTo = viewHolder.itemView.imageViewPhotoTo

        db.collection("User")
            .document(message.senderId!!)
            .get()
            .addOnSuccessListener { result ->

                val user = result.toObject(User::class.java)

                if(!details){

                    textViewName.visibility = View.INVISIBLE
                    imageViewPhotoTo.visibility = View.INVISIBLE

                }else{

                    textViewName.text = user!!.username

                    Picasso.get().load(user.imageURL).into(imageViewPhotoTo)

                }

                textViewChatMessageTo.text = "${user!!.username} removeu esta mensagem"

            }
    }

    override fun getLayout(): Int {
        return R.layout.row_removed_message_to
    }
}

class HourItem(val date: Date): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewHour = viewHolder.itemView.textViewHour

        textViewHour.text = Utils.getMessageDate(date)

    }

    override fun getLayout(): Int {
        return R.layout.row_hour_message
    }
}

class FileItemFrom(val message: ChatMessage): Item<ViewHolder>(){

    val db = FirebaseFirestore.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewFileNameFrom = viewHolder.itemView.textViewFileNameFrom
        val textViewFileTypeFrom = viewHolder.itemView.textViewFileTypeFrom
        val imageViewFileFrom = viewHolder.itemView.imageViewFileFrom

        db.collection("Files")
            .document(message.text!!)
            .get()
            .addOnSuccessListener { fileObject ->

                val file = fileObject.toObject(FileModel::class.java)

                when(file!!.extension)
                {
                    "pdf" -> {

                        imageViewFileFrom.setImageResource(R.drawable.pdf)

                    }

                    "docx" -> {

                        imageViewFileFrom.setImageResource(R.drawable.word)

                    }

                    else -> {

                        imageViewFileFrom.setImageResource(R.drawable.file)

                    }

                }

                textViewFileNameFrom.text = file!!.name
                textViewFileTypeFrom.text = file.extension

            }

    }

    override fun getLayout(): Int {
        return R.layout.row_file_from
    }
}

class FileItemTo(val message: ChatMessage, val details: Boolean): Item<ViewHolder>(){

    val db = FirebaseFirestore.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewFileNameTo = viewHolder.itemView.textViewFileNameTo
        val textViewFileTypeTo = viewHolder.itemView.textViewFileTypeTo
        val imageViewPhotoFileTo = viewHolder.itemView.imageViewPhotoFileTo
        val textViewNameFileTo = viewHolder.itemView.textViewNameFileTo
        val imageViewFileTo = viewHolder.itemView.imageViewFileTo

        val refUser = db.collection("User")
            .document(message.senderId!!)

        refUser.get().addOnSuccessListener { result ->
            val user = result.toObject(User::class.java)

            if(!details){

                textViewNameFileTo.visibility = View.INVISIBLE
                imageViewPhotoFileTo.visibility = View.INVISIBLE

            }else{

                textViewNameFileTo.text = user!!.username

                Picasso.get().load(user.imageURL).into(imageViewPhotoFileTo)

            }

        }

        db.collection("Files")
            .document(message.text!!)
            .get()
            .addOnSuccessListener { fileObject ->

                val file = fileObject.toObject(FileModel::class.java)

                textViewFileNameTo.text = file!!.name
                textViewFileTypeTo.text = file.extension

                when(file!!.extension)
                {
                    "pdf" -> {

                        imageViewFileTo.setImageResource(R.drawable.pdf)

                    }

                    "docx" -> {

                        imageViewFileTo.setImageResource(R.drawable.word)

                    }

                    else -> {

                        imageViewFileTo.setImageResource(R.drawable.file)

                    }

                }

            }

    }

    override fun getLayout(): Int {
        return R.layout.row_file_to
    }
}