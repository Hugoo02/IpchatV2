package ipca.project.ipchatv2.Chat

import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.LastMessage
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import kotlinx.android.synthetic.main.row_last_messages.view.*

class LatestMessageRow(val lastMessage: LastMessage): Item<ViewHolder>(){

    //var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {

        var message : ChatMessage? = null
        var senderUser : User? = null

        val db = FirebaseFirestore.getInstance()

        val refMessage = db.collection("chatChannels")
            .document(lastMessage.groupId.toString())
            .collection("messages")
            .document(lastMessage.messageId.toString())

        val textViewUserNameLM = viewHolder.itemView.textViewUserNameLM
        val textViewLatestMessage = viewHolder.itemView.textViewLatestMessage

        val circleImageUserLM = viewHolder.itemView.circleImageUserLM


        refMessage.get().addOnSuccessListener { result ->

            message = result.toObject(ChatMessage::class.java)
            //val message = ChatMessage.fromHash(result.data as HashMap<String, Any?>)

            textViewLatestMessage.text = message!!.text

            val refSenderUser = db.collection("User")
                .document(message!!.senderId.toString())

            refSenderUser.get().addOnSuccessListener { result ->

                senderUser = result.toObject(User::class.java)

                println(result.data)

                textViewUserNameLM.text = senderUser!!.username

                Picasso.get().load(senderUser!!.imageURL).into(circleImageUserLM)

            }

        }

    }

    override fun getLayout(): Int {
        return R.layout.row_last_messages
    }
}