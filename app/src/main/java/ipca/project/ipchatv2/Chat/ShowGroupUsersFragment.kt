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

                    //Snapshot importante para verificar se houve mudanças nos membros do grupo

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

                                            val iter: ListIterator<UserItem> =
                                                adapterList.listIterator()

                                            val toReplace = mutableMapOf<UserItem, Int>()

                                            while (iter.hasNext()) {
                                                val str = iter.next()
                                                val index = iter.nextIndex()

                                                println("strId = " + str.user.id)
                                                println("index = " + index)

                                                if((user!!.id == str.user.id && !admin == str.admin))
                                                {
                                                    val newElement = UserItem(str.user, admin)

                                                    toReplace[newElement] = index - 1
                                                    passed = true

                                                }
                                                else if ((user.id == str.user.id && admin == str.admin)){

                                                    passed = true

                                                }//else if ((str.user.id!! !in group.userIds!!)){

                                                //    toDelete.add(str)
                                                //    passed = true

                                                //}
                                            }

                                            if(!passed)
                                            {

                                                adapterList.add(UserItem(user!!, admin))

                                            }

                                           toReplace.forEach {

                                               adapterList[it.value] = it.key

                                           }

                                            refreshAdapter()
                                        }

                                }else{

                                    val iter: ListIterator<UserItem> =
                                        adapterList.listIterator()

                                    val toDelete : MutableList<UserItem> = arrayListOf()

                                    while (iter.hasNext()) {
                                        val str = iter.next()

                                        if(str.user.id == id)
                                            toDelete.add(str)

                                    }

                                    adapterList.removeAll(toDelete)

                                    refreshAdapter()

                                }

                            }
                    }

                }

        }else {

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

                                if (groupObject!!["admin"] != null) {

                                    val admin = groupObject["admin"] as Boolean

                                    if(admin){

                                        db.collection("User")
                                            .document(id)
                                            .get()
                                            .addOnSuccessListener { userObject ->

                                                val user = userObject.toObject(User::class.java)

                                                var passed = false

                                                val iter: ListIterator<UserItem> =
                                                    adapterList.listIterator()

                                                val toDelete : MutableList<UserItem> = arrayListOf()

                                                while (iter.hasNext()) {
                                                    val str = iter.next()
                                                    val index = iter.nextIndex()

                                                    println("strId = " + str.user.id)
                                                    println("index = " + index)

                                                    if ((str.user.id!! !in group.userIds!!)) {

                                                        toDelete.add(str)
                                                        passed = true

                                                    } else if (user!!.id == str.user.id && admin == str.admin)
                                                        passed = true
                                                }

                                                if (!passed) {

                                                    adapterList.add(UserItem(user!!, admin))

                                                }

                                                refreshAdapter()

                                            }

                                    } else {

                                        val iter: ListIterator<UserItem> =
                                            adapterList.listIterator()

                                        val toDelete : MutableList<UserItem> = arrayListOf()

                                        while (iter.hasNext()) {
                                            val str = iter.next()

                                            if(str.user.id == id)
                                                toDelete.add(str)

                                        }

                                        adapterList.removeAll(toDelete)

                                        refreshAdapter()

                                    }

                                } else {

                                    val iter: ListIterator<UserItem> =
                                        adapterList.listIterator()

                                    val toDelete : MutableList<UserItem> = arrayListOf()

                                    while (iter.hasNext()) {
                                        val str = iter.next()

                                        if(str.user.id == id)
                                            toDelete.add(str)

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