package ipca.project.ipchatv2.Chat

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.*
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.Utils.Utils
import kotlinx.android.synthetic.main.row_last_messages.view.*

class GroupListLMRow(val messageGroup: MessageGroup): Item<ViewHolder>(){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().uid

        //Referencia responsável por buscar as informações da conversa da row
        val refGroup = db.collection("groupChannels")
            .document(messageGroup.groupId.toString())

        val refMessage = db.collection("groupChannels")
            .document(messageGroup.groupId.toString())
            .collection("messages")
            .document(messageGroup.messageId.toString())

        println("messageId = " + messageGroup.messageId.toString())

        val textViewChatNameLM = viewHolder.itemView.textViewChatNameLM
        val textViewMessageLM = viewHolder.itemView.textViewMessageLM
        val textViewHourLM = viewHolder.itemView.textViewHourLM

        val circleImageLM = viewHolder.itemView.circleImageLM

        refGroup.get().addOnSuccessListener { result ->

            val group = result.toObject(GroupChannel::class.java)

            textViewHourLM.text = Utils.formatDateToChat(messageGroup.time!!)

            textViewChatNameLM.text = group!!.chatName

            Picasso.get().load(group.groupImageURL).into(circleImageLM)

        }

        refMessage.get().addOnSuccessListener { result ->

            val message = result.toObject(ChatMessage::class.java)

            if(message!!.type == "firstMessage")
                textViewMessageLM.text = "Novo Grupo"
            else if(message.senderId == currentUser && message.type == "TEXT")
                textViewMessageLM.text = "Tu: ${message.text}"
            else if(message.senderId == currentUser && message.type == "IMAGE")
                textViewMessageLM.text = "Tu enviaste uma fotografia"
            else if(message.senderId == currentUser && message.type == "REMOVED")
                textViewMessageLM.text = "Tu removeste uma mensagem"
            else if(message.senderId == currentUser && message.type == "FILE")
                textViewMessageLM.text = "Tu enviaste um ficheiro"
            else{

                val refSenderUser = db.collection("User")
                    .document(message.senderId.toString())

                refSenderUser.get().addOnSuccessListener { result ->

                    val senderUser = result.toObject(User::class.java)

                    if(message.type == "TEXT")
                        textViewMessageLM.text = "${senderUser!!.username}: ${message.text}"
                    else if(message.type == "IMAGE")
                        textViewMessageLM.text = "${senderUser!!.username} enviou uma fotografia"
                    else if(message.type == "REMOVED")
                        textViewMessageLM.text = "${senderUser!!.username} removeu uma mensagem"
                    else if(message.type == "FILE")
                        textViewMessageLM.text = "${senderUser!!.username} removeu uma mensagem"
                }

            }

        }

    }

    override fun getLayout(): Int {
        return R.layout.row_last_messages
    }
}