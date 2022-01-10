package ipca.project.ipchatv2.CustomDialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import ipca.project.ipchatv2.Chat.ChatActivity
import ipca.project.ipchatv2.MainActivity
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.ShowUsersActivity
import ipca.project.ipchatv2.databinding.FragmentChooseImageBinding
import ipca.project.ipchatv2.databinding.FragmentShowGroupUsersBinding
import kotlinx.android.synthetic.main.activity_show_users.*
import java.util.*

class ChooseImageFragment : DialogFragment() {

    private lateinit var binding: FragmentChooseImageBinding

    private var groupId : String? = null

    private var selectedPhotoUri: Uri? = null

    val db = FirebaseFirestore.getInstance()

    val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

        selectedPhotoUri = it.data?.data

        binding.circleImagePhoto.setImageURI(null)
        binding.circleImagePhoto.setImageURI(selectedPhotoUri)
        binding.buttonSelectPhoto.alpha = 0f

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString("groupId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseImageBinding.inflate(layoutInflater)

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.buttonSelectPhoto.setOnClickListener {

            selectImage()

        }

        binding.buttonSubmit.setOnClickListener {

            uploadImageToFirebaseStorage()
            startChatActivity(groupId!!)
            dismiss()

        }

        return binding.root
    }

    private fun startChatActivity(channelId: String){

        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("groupId", channelId)
        intent.putExtra("channelType", "group")
        startActivity(intent)
        requireActivity().finish()

    }

    private fun selectImage() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getImage.launch(intent)

    }

    private fun uploadImageToFirebaseStorage(){

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                println("Image uploaded successfully: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {

                    sendImageToDB(it.toString())

                }

            }

    }

    private fun sendImageToDB(imageUrl: String) {

        db.collection("groupChannels")
            .document(groupId!!)
            .update(mapOf("groupImageURL" to imageUrl))

    }
}