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
import ipca.project.ipchatv2.Models.User
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
    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

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

            result.documents.forEach{

                var groupId : String? = null

                it.data!!.values.forEach{

                    groupId = it.toString()

                }

                val refLastMessageId = db.collection("chatChannels").document(groupId!!)
                    .collection("lastMessage")

                //Referencia responsável por resgatar as ultimas mensagens de todos os grupos em que o user pertence

                refLastMessageId.addSnapshotListener{ documents, e ->

                    documents?.let {

                        for (document in it){

                            //Referencia responsável por listar todos os utilizadores da sala em questão
                            val refMembers = db.collection("chatChannels")
                                .document(groupId!!)

                            var usersList : MutableList<String> = ArrayList()
                            var otherUserId : String? = null

                            refMembers.get().addOnSuccessListener { result ->

                                result.data!!.forEach{ result ->

                                    usersList = result.value as MutableList<String>

                                    //Se o grupo tiver 2 membros, ou seja grupos individuais
                                    if(usersList.size == 2){

                                        if(usersList[0] == currentUserId)
                                            otherUserId = usersList[1]
                                        else
                                            otherUserId = usersList[0]

                                        val lastMessage = document.toObject(LastMessage::class.java)
                                        groupList.add(LastMessage(groupId, otherUserId, document.id, lastMessage.time))
                                        println("groupList = " + groupList)

                                    }

                                }

                                println("groupList = " + groupList)
                                groupList.sortByDescending{it.time}
                                refreshAdapter()
                            }

                        }

                    }

                }

            }
        }
    }

    private fun refreshAdapter() {
        adapter.clear()
        println("passou1 " + groupList.size)
        groupList.forEach {
            println("passou")
            adapter.add(LatestMessageRow(it))
        }
    }
}
