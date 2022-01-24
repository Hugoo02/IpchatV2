package ipca.project.ipchatv2.Chat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.RowConfigurations.EmptyItem
import ipca.project.ipchatv2.RowConfigurations.PhotoItem
import ipca.project.ipchatv2.databinding.FragmentShowPhotosBinding
import ipca.project.ipchatv2.databinding.FragmentUserListBinding
import java.io.Serializable
import java.lang.ref.Reference

class ShowPhotosFragment : Fragment() {

    private lateinit var binding: FragmentShowPhotosBinding

    var groupId : String? = null
    var channelType: String? = null

    val db = FirebaseFirestore.getInstance()

    val adapter = GroupAdapter<ViewHolder>().apply {
        spanCount = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString("groupId")
            channelType = it.getString("channelType")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowPhotosBinding.inflate(layoutInflater)

        val recyclerViewShowPhotos = binding.recyclerViewShowPhotos

        recyclerViewShowPhotos.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        recyclerViewShowPhotos.adapter = adapter

        adapter.setOnItemClickListener { item, view ->

            val row = item as PhotoItem

            val intent = Intent(requireContext(), ShowPhotoCloserActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("channelType", channelType)
            intent.putExtra("message", row.message)
            startActivity(intent)

        }

        listenForPhotos()

        return binding.root
    }

    private fun listenForPhotos() {

        var groupNameRef : String? = null

        if(channelType == "group")
            groupNameRef = "groupChannels"
        else
            groupNameRef = "privateChannels"

        db.collection(groupNameRef)
            .document(groupId!!)
            .collection("messages")
            .get()
            .addOnSuccessListener { result ->

                var empty = true

                for(doc in result.documents){

                    println("passou aqui")

                    val message = doc.toObject(ChatMessage::class.java)

                    if(message!!.type == "IMAGE")
                    {
                        empty = false
                        adapter.spanCount = 3
                        binding.recyclerViewShowPhotos.layoutManager = GridLayoutManager(requireContext(), 3)

                        adapter.add(PhotoItem(message))
                    }


                }

                if(empty)
                {

                    adapter.spanCount = 1
                    binding.recyclerViewShowPhotos.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL ,false)
                    adapter.add(EmptyItem())

                }


            }

    }

}