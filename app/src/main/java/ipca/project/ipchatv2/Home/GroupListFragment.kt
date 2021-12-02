package ipca.project.ipchatv2.Home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Chat.ChatActivity
import ipca.project.ipchatv2.Chat.GroupListLMRow
import ipca.project.ipchatv2.Chat.UserListLMRow
import ipca.project.ipchatv2.Models.LastMessage
import ipca.project.ipchatv2.Models.LastMessageGroup
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.UserItem
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import ipca.project.ipchatv2.databinding.FragmentGroupListBinding
import ipca.project.ipchatv2.databinding.FragmentUserListBinding
import java.util.ArrayList

class GroupListFragment : Fragment() {
    private lateinit var binding: FragmentGroupListBinding

    val adapter = GroupAdapter<ViewHolder>()
    private var db = FirebaseFirestore.getInstance()
    val groupList : MutableList<LastMessageGroup> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupListBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        binding.recyclerViewGroupLM.adapter = adapter
        binding.recyclerViewGroupLM.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        listenForLatestMessages()

        //set item click listener on the adapter
        adapter.setOnItemClickListener{item, view ->

            val row = item as GroupListLMRow

            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("groupId", row.lastMessageGroup.groupId)
            intent.putExtra("channelType", "group")
            startActivity(intent)

        }


        return binding.root
    }

    private fun listenForLatestMessages() {

        val currentUserId = FirebaseAuth.getInstance().uid
        val refIdGroups = db.collection("User")
            .document(currentUserId!!)
            .collection("groupChannel")

        //Referencia responsável por resgatar todos os grupos do utilizador
        refIdGroups.get().addOnSuccessListener { result ->

            result.documents.forEach {

                val groupId = it.id

                val refLastMessageId = db.collection("groupChannels").document(groupId)
                    .collection("lastMessage")

                //Referencia responsável por resgatar as ultimas mensagens de todos os grupos em que o user pertence

                refLastMessageId.addSnapshotListener{ documents, e ->

                    adapter.clear()
                    groupList.clear()

                    documents?.let {

                        for (document in it){

                            val lastMessageId = document.id
                            val lastMessageGroup = document.toObject(LastMessageGroup::class.java)
                            groupList.add(
                                LastMessageGroup(groupId, lastMessageId, lastMessageGroup.time)
                            )

                            groupList.sortByDescending{it.time}
                            refreshAdapter()
                        }

                    }

                }

            }

        }

    }

    private fun refreshAdapter() {
        adapter.clear()
        groupList.forEach {
            adapter.add(GroupListLMRow(it))
        }
    }

}