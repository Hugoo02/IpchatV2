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
import ipca.project.ipchatv2.Models.MessageGroup
import ipca.project.ipchatv2.databinding.FragmentGroupListBinding

class GroupListFragment : Fragment() {
    private lateinit var binding: FragmentGroupListBinding

    val adapter = GroupAdapter<ViewHolder>()
    private var db = FirebaseFirestore.getInstance()
    val groupList = HashMap<String, MessageGroup>()

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
            intent.putExtra("groupId", row.messageGroup.groupId)
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

                    documents?.let {

                        for (document in it){

                            val lastMessageId = document.id
                            val lastMessageGroup = document.toObject(MessageGroup::class.java)
                            val message = MessageGroup(groupId, lastMessageId, lastMessageGroup.time)

                            println("passou no snapshot")
                            groupList[message.groupId!!] = message
                            refreshAdapter()
                        }

                        //MELHOR AQUI SECALHAR, TESTAR QUANDO EXISTIREM MAIS GRUPOS
                        //groupList.values.sortedByDescending { it.time }
                        //refreshAdapter()

                    }

                }

            }

        }

    }

    private fun refreshAdapter() {
        adapter.clear()
        val messages = groupList.values.sortedByDescending { it.time }
        messages.forEach {
            adapter.add(GroupListLMRow(it))
        }
    }

}