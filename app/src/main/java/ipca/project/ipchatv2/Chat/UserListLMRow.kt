package ipca.project.ipchatv2.Chat

import android.graphics.Color
import android.graphics.Color.GRAY
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.MessagePrivate
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.Utils.Utils
import kotlinx.android.synthetic.main.row_last_messages.view.*

class UserListLMRow(val message: MessagePrivate): Item<ViewHolder>(){

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bind(viewHolder: ViewHolder, position: Int) {

        var message : ChatMessage? = null
        var otherUser : User? = null
        val currentUser = FirebaseAuth.getInstance().uid

        val db = FirebaseFirestore.getInstance()

        //Referencia responsável por buscar as informações da conversa da row
        val refMessage = db.collection("privateChannels")
            .document(this.message.groupId.toString())
            .collection("messages")
            .document(this.message.messageId.toString())

        val textViewChatNameLM = viewHolder.itemView.textViewChatNameLM
        val textViewMessageLM = viewHolder.itemView.textViewMessageLM
        val textViewHourLM = viewHolder.itemView.textViewHourLM

        val circleImageLM = viewHolder.itemView.circleImageLM
        var onlineBall = viewHolder.itemView.onlineBall


        refMessage.get().addOnSuccessListener { result ->

            message = result.toObject(ChatMessage::class.java)

            textViewHourLM.text = Utils.formatDateToChat(this.message.time!!)

            //Busca pelo utilizador visitante da sala de chat
            val refUserChat = db.collection("User")
                .document(this.message.otherUserId!!)

            refUserChat.get().addOnSuccessListener { result ->

                otherUser = result.toObject(User::class.java)

                if(otherUser!!.status == true){
                    onlineBall.visibility = View.VISIBLE
                }
                else{
                    onlineBall.visibility = View.GONE
                }

                textViewChatNameLM.text = otherUser!!.username

                Picasso.get().load(otherUser!!.imageURL).into(circleImageLM)

                if(message!!.senderId == currentUser && message!!.type == "TEXT")
                    textViewMessageLM.text = "Tu: ${message!!.text}"
                else if(message!!.type == "TEXT")
                    textViewMessageLM.text = "${otherUser!!.username}: ${message!!.text}"
                else if(message!!.senderId == currentUser && message!!.type == "IMAGE")
                    textViewMessageLM.text = "Tu enviaste uma fotografia"
                else if(message!!.type == "IMAGE")
                    textViewMessageLM.text = "${otherUser!!.username} enviou uma fotografia"
                else if(message!!.senderId == currentUser && message!!.type == "REMOVED")
                    textViewMessageLM.text = "Tu removeste uma mensagem"
                else if(message!!.type == "REMOVED")
                    textViewMessageLM.text = "${otherUser!!.username} removeu uma mensagem"
                else if(message!!.senderId == currentUser && message!!.type == "FILE")
                    textViewMessageLM.text = "Tu enviaste um ficheiro"
                else if(message!!.type == "FILE")
                    textViewMessageLM.text = "${otherUser!!.username} removeu uma mensagem"

            }

        }

    }

    override fun getLayout(): Int {
        return R.layout.row_last_messages
    }
}