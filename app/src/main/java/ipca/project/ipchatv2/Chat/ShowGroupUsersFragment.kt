package ipca.project.ipchatv2.Chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    var passou = false

    val db = FirebaseFirestore.getInstance()

    val adapter = GroupAdapter<ViewHolder>()

    val adapterList : MutableList<UserItem> = arrayListOf()

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
                .addSnapshotListener { result, error ->

                    adapter.clear()
                    adapterList.clear()

                    val group = result!!.toObject(GroupChannel::class.java)

                    group!!.userIds!!.forEachIndexed { index, id ->

                        db.collection("User")
                            .document(id)
                            .collection("groupChannels")
                            .document(groupId!!)
                            .addSnapshotListener { groupObject, error ->

                                if(groupObject!!["admin"] != null)
                                {

                                    val admin = groupObject["admin"] as Boolean

                                    db.collection("User")
                                        .document(id)
                                        .get()
                                        .addOnSuccessListener { userObject ->

                                            val user = userObject.toObject(User::class.java)

                                            var passed = false

                                            adapterList.forEachIndexed { index, userItem ->

                                                if ((user!!.id == userItem.user.id && !admin == userItem.admin))
                                                {
                                                    val newElement = UserItem(userItem.user, admin)

                                                    adapterList[index] = newElement
                                                    passed = true

                                                }else if ((userItem.user.id!! !in group.userIds!!)){

                                                    adapterList.removeAt(index)
                                                    passed = true

                                                }
                                                else if ((user.id == userItem.user.id && admin == userItem.admin)){

                                                    passed = true

                                                }

                                            }

                                            if(!passed)
                                            {

                                                adapterList.add(UserItem(user!!, admin))

                                            }

                                            refreshAdapter()
                                        }

                                }else{

                                    adapterList.forEachIndexed { index, userItem ->

                                        if(userItem.user.id == id)
                                            adapterList.removeAt(index)

                                    }

                                    refreshAdapter()

                                }

                            }
                    }

                }

        }else{

            db.collection("groupChannels")
                .document(groupId!!)
                .addSnapshotListener { result, error ->

                    adapter.clear()
                    adapterList.clear()

                    val group = result!!.toObject(GroupChannel::class.java)

                    group!!.userIds!!.forEachIndexed { index, id ->

                        db.collection("User")
                            .document(id)
                            .collection("groupChannels")
                            .document(groupId!!)
                            .addSnapshotListener { groupObject, error ->

                                if(groupObject!!["admin"] != null)
                                {

                                    val admin = groupObject["admin"] as Boolean

                                    if(admin){

                                        db.collection("User")
                                            .document(id)
                                            .get()
                                            .addOnSuccessListener { userObject ->

                                                val user = userObject.toObject(User::class.java)

                                                var passed = false

                                                adapterList.forEachIndexed { index, userItem ->

                                                    if ((userItem.user.id!! !in group.userIds!!)){

                                                        adapterList.removeAt(index)
                                                        passed = true

                                                    }else if(user!!.id == userItem.user.id && admin == userItem.admin)
                                                        passed = true

                                                }

                                                if(!passed){

                                                    adapterList.add(UserItem(user!!, admin))

                                                }

                                                refreshAdapter()

                                            }

                                    }else{

                                        adapterList.forEachIndexed { index, userItem ->

                                            println("id = $id")
                                            println("userItem.user.id = ${userItem.user.id}")

                                            if(id == userItem.user.id)
                                            {

                                                println("passou aqui")
                                                adapterList.removeAt(index)

                                            }


                                        }

                                        refreshAdapter()

                                    }

                                }else{

                                    adapterList.forEachIndexed { index, userItem ->

                                        if(userItem.user.id == id)
                                            adapterList.removeAt(index)

                                    }

                                    refreshAdapter()

                                }

                            }
                    }

                }

        }

        adapter.setOnItemClickListener{ item, view ->

            val row = item as UserItem

            val bundle = Bundle()
            bundle.putString("userId", row.user.id)
            bundle.putBoolean("admin", row.admin)
            bundle.putString("groupId", groupId)
            val dialog = ProfileDelaisFragment()
            dialog.arguments = bundle

            dialog.show(parentFragmentManager, "customDialog")

        }

        return binding.root
    }

    private fun refreshAdapter() {

        adapter.clear()

        adapterList.forEach {

            println("it.user.id = " + it.user.id)
            println("it.admin = " + it.admin)

            adapter.add(it)

        }

    }
}