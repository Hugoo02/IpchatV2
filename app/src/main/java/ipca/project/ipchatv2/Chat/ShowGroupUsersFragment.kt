package ipca.project.ipchatv2.Chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.CustomDialogs.ProfileDelaisFragment
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.RowConfigurations.UserItem
import ipca.project.ipchatv2.databinding.FragmentShowGroupUsersBinding

class ShowGroupUsersFragment : Fragment() {

    private lateinit var binding: FragmentShowGroupUsersBinding

    private var groupId: String? = null
    private var userType: String? = null

    val db = FirebaseFirestore.getInstance()

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString("groupId")
            userType = it.getString("userType")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowGroupUsersBinding.inflate(layoutInflater)

        binding.recyclerViewShowUsers.adapter = adapter

        if(userType == "All"){

            db.collection("groupChannels")
                .document(groupId!!)
                .get().addOnSuccessListener { result ->

                    val group = result.toObject(GroupChannel::class.java)

                    group!!.userIds!!.forEach {

                        db.collection("User")
                            .document(it)
                            .collection("groupChannels")
                            .document(groupId!!)
                            .get()
                            .addOnSuccessListener { groupObject ->

                                val admin = groupObject["admin"] as Boolean

                                db.collection("User")
                                    .document(it)
                                    .get()
                                    .addOnSuccessListener { userObject ->

                                        val user = userObject.toObject(User::class.java)

                                        adapter.add(UserItem(user!!, admin))
                                    }

                            }

                    }

                }

        }else{

            db.collection("groupChannels")
                .document(groupId!!)
                .get().addOnSuccessListener { result ->

                    val group = result.toObject(GroupChannel::class.java)

                    group!!.userIds!!.forEach {

                        db.collection("User")
                            .document(it)
                            .collection("groupChannels")
                            .document(groupId!!)
                            .get()
                            .addOnSuccessListener { groupObject ->

                                val admin = groupObject["admin"] as Boolean

                                if(admin){

                                    db.collection("User")
                                        .document(it)
                                        .get()
                                        .addOnSuccessListener { userObject ->

                                            val user = userObject.toObject(User::class.java)

                                            adapter.add(UserItem(user!!, admin))
                                        }

                                }

                            }
                    }

                }

        }

        adapter.setOnItemClickListener{ item, view ->

            val row = item as UserItem

            val bundle = Bundle()
            bundle.putString("userId", row.user.id)
            val dialog = ProfileDelaisFragment()
            dialog.arguments = bundle

            dialog.show(parentFragmentManager, "customDialog")

        }

        return binding.root
    }
}