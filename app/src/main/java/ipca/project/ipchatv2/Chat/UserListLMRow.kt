package ipca.project.ipchatv2.Chat

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.LastMessage
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.Utils
import kotlinx.android.synthetic.main.row_last_messages.view.*

class UserListLMRow(val lastMessage: LastMessage): Item<ViewHolder>(){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bind(viewHolder: ViewHolder, position: Int) {

        var message : ChatMessage? = null
        var otherUser : User? = null
        val currentUser = FirebaseAuth.getInstance().uid

        val db = FirebaseFirestore.getInstance()

        //Referencia responsável por buscar as informações da conversa da row
        val refMessage = db.collection("chatChannels")
            .document(lastMessage.groupId.toString())
            .collection("messages")
            .document(lastMessage.messageId.toString())

        val textViewChatNameLM = viewHolder.itemView.textViewChatNameLM
        val textViewMessageLM = viewHolder.itemView.textViewMessageLM
        val textViewHourLM = viewHolder.itemView.textViewHourLM

        val circleImageLM = viewHolder.itemView.circleImageLM

        refMessage.get().addOnSuccessListener { result ->

            message = result.toObject(ChatMessage::class.java)

            textViewHourLM.text = Utils.formatDateToChat(lastMessage.time!!)

            //Busca pelo utilizador visitante da sala de chat
            val refUserChat = db.collection("User")
                .document(lastMessage.otherUserId!!)

            refUserChat.get().addOnSuccessListener { result ->

                otherUser = result.toObject(User::class.java)

                println(result.data)

                textViewChatNameLM.text = otherUser!!.username

                Picasso.get().load(otherUser!!.imageURL).into(circleImageLM)

                if(message!!.senderId == currentUser)
                    textViewMessageLM.text = "Tu: ${message!!.text}"
                else
                    textViewMessageLM.text = "${otherUser!!.username}: ${message!!.text}"

            }

        }

    }

    override fun getLayout(): Int {
        return R.layout.row_last_messages
    }
}