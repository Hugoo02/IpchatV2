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
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

class ShowPhotosFragment : Fragment() {

    private lateinit var binding: FragmentShowPhotosBinding

    var groupId : String? = null
    var channelType: String? = null

    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U="
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA=="

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

                    if(message!!.text != null){
                        var string1 :String

                        val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

                        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                        val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
                        val tmp = factory.generateSecret(spec);
                        val secretKey =  SecretKeySpec(tmp.encoded, "AES")

                        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
                        string1 = String(cipher.doFinal(Base64.decode(message!!.text, Base64.DEFAULT)))


                        message.text = string1
                    }


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