package ipca.project.ipchatv2.Home

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Chat.ChatActivity
import ipca.project.ipchatv2.Chat.LatestMessageRow
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.LastMessage
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.UserItem
import ipca.project.ipchatv2.databinding.FragmentUserListBinding
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.row_last_messages.view.*
import kotlinx.android.synthetic.main.row_users.view.*
import java.util.*

class UserListFragment : Fragment() {
    private lateinit var binding: FragmentUserListBinding
    private var db = FirebaseFirestore.getInstance()
    val adapter = GroupAdapter<ViewHolder>()
    val groupList : MutableList<LastMessage> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "IPCHAT"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.inflate(layoutInflater)

        binding.recyclerViewShowUsers.adapter = adapter
        binding.recyclerViewShowUsers.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        listenForLatestMessages()

        adapter.setOnItemClickListener { item, view ->

            var userItem = item as UserItem

            val intent = Intent(view.context, ChatActivity::class.java)
            //intent.putExtra("User", userItem.user)
            //intent.flags()
            startActivity(intent)

        }

        return binding.root
    }


    private fun listenForLatestMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val refIdGroups = db.collection("User")
            .document("$fromId")
            .collection("engagedChatChannels")

        //Referencia responsável por resgatar todos os grupos do user em questão

        refIdGroups.get().addOnSuccessListener { result ->

            println("docuemntos = " + result.documents)

            result.documents.forEach{

                val groupId = it.id

                println("groupId = ${it.id}")

                val refLastMessageId = db.collection("chatChannels").document(it.id)
                    .collection("lastMessage")

                //Referencia responsável por resgatar as ultimas mensagens de todos os grupos em que o user pertence

                refLastMessageId.addSnapshotListener{ documents, e ->

                    documents?.let {

                        for (document in it){

                            val lastMessage = document.toObject(LastMessage::class.java)
                            println("passou antes de adicionar")
                            groupList.add(LastMessage(groupId, document.id, lastMessage.time))
                            println(groupList[0].messageId)

                        }

                        groupList.sortByDescending{it.time}

                    }

                    refreshAdapter()

                }

            }
        }
    }

    private fun refreshAdapter() {
        adapter.clear()
        println("passou no adapter")
        groupList.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }
}
