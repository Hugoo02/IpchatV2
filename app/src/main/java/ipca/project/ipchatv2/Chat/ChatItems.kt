package ipca.project.ipchatv2.Chat

import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import kotlinx.android.synthetic.main.row_first_message.view.*
import kotlinx.android.synthetic.main.row_text_message_from.view.*
import kotlinx.android.synthetic.main.row_text_message_to.view.*


class ChatFromItem(val message: ChatMessage): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewChatMessageFrom = viewHolder.itemView.textViewChatMessageFrom

        textViewChatMessageFrom.text = message.text

    }

    override fun getLayout(): Int {
        return R.layout.row_text_message_from
    }
}


class ChatToItem(val message: ChatMessage): Item<ViewHolder>(){

    val db = FirebaseFirestore.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewChatMessageTo = viewHolder.itemView.textViewChatMessageTo
        val imageViewPhotoTo = viewHolder.itemView.imageViewPhotoTo

        textViewChatMessageTo.text = message.text

        val refUser = db.collection("User")
            .document(message.senderId!!)

        refUser.get().addOnSuccessListener { result ->
            val user = result.toObject(User::class.java)

            Picasso.get().load(user!!.imageURL).into(imageViewPhotoTo)

        }
    }

    override fun getLayout(): Int {
        return R.layout.row_text_message_to
    }
}

class FirstMessage(val message: ChatMessage): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewFirstMessageHour = viewHolder.itemView.textViewFirstMessageHour

    }

    override fun getLayout(): Int {
        return R.layout.row_first_message
    }
}