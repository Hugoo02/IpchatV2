package ipca.project.ipchatv2.Home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Chat.ChatActivity
import ipca.project.ipchatv2.Chat.UserListLMRow
import ipca.project.ipchatv2.Models.LastMessagePrivate
import ipca.project.ipchatv2.ShowUsersActivity
import ipca.project.ipchatv2.databinding.FragmentUserListBinding
import java.util.*
import kotlin.collections.HashMap

class UserListFragment : Fragment() {
    private lateinit var binding: FragmentUserListBinding
    private var db = FirebaseFirestore.getInstance()
    val adapter = GroupAdapter<ViewHolder>()
    //val groupList : MutableList<LastMessagePrivate> = arrayListOf()
    val groupList = HashMap<String, LastMessagePrivate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "IPCHAT"

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.inflate(layoutInflater)

        binding.recyclerViewUserLM.adapter = adapter
        binding.recyclerViewUserLM.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        listenForLatestMessages()

        binding.buttonNewMessage.setOnClickListener {

            val getResult =
                registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) {
                    if (it.resultCode == Activity.RESULT_OK) {
                        val id = it.data?.getStringExtra("id")
                    }
                }

            val intent = Intent(activity, ShowUsersActivity::class.java)
            getResult.launch(intent)

        }

        //set item click listener on the adapter
        adapter.setOnItemClickListener{item, view ->

            val row = item as UserListLMRow

            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("groupId", row.lastMessage.groupId)
            intent.putExtra("channelType", "private")
            startActivity(intent)

        }

        return binding.root
    }


    private fun listenForLatestMessages() {

        val currentUserId = FirebaseAuth.getInstance().uid
        val refIdGroups = db.collection("User")
            .document("$currentUserId")
            .collection("engagedChatChannels")

        //Referencia responsável por resgatar todos os grupos do user em questão

        refIdGroups.get().addOnSuccessListener { result ->

            result.documents.forEach{

                var groupId : String? = null

                it.data!!.values.forEach{

                    groupId = it.toString()

                }

                //Referencia responsável por listar todos os utilizadores da sala em questão
                val refMembers = db.collection("chatChannels")
                    .document(groupId!!)

                var usersList : MutableList<String> = ArrayList()
                var otherUserId : String? = null

                refMembers.get().addOnSuccessListener { result ->

                    result.data!!.forEach{ result ->

                        usersList = result.value as MutableList<String>

                        //Se o grupo tiver 2 membros, ou seja grupos individuais

                        if(usersList[0] == currentUserId)
                            otherUserId = usersList[1]
                        else
                            otherUserId = usersList[0]


                    }

                val refLastMessageId = db.collection("chatChannels").document(groupId!!)
                    .collection("lastMessage")

                //Referencia responsável por resgatar as ultimas mensagens de todos os grupos em que o user pertence

                refLastMessageId.addSnapshotListener{ documents, e ->

                    documents?.let {

                        var lastMessage: LastMessagePrivate? = null

                        var message : LastMessagePrivate? = null

                        for (document in it){

                            println("2. otherUserId = $otherUserId")

                            lastMessage = document.toObject(LastMessagePrivate::class.java)

                            message = LastMessagePrivate(groupId, otherUserId, document.id, lastMessage.time)

                            println("2. group = ${message.groupId}")

                            groupList[message.groupId!!] = message

                            }

                        refreshAdapter()

                        //Não está a ordenar direito
                            println("3. otherUserId = $otherUserId")

                        }

                    }

                }

            }
        }
    }

    private fun refreshAdapter() {
        adapter.clear()
        val messages = groupList.values.sortedByDescending { it.time }
        messages.forEach {
            adapter.add(UserListLMRow(it))
        }
    }
}
