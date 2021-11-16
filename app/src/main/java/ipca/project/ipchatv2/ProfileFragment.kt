package ipca.project.ipchatv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.FragmentProfileBinding
import java.util.*

class ProfileFragment : Fragment(R.layout.fragment_profile)  {

    //Variables
    lateinit var binding: FragmentProfileBinding
    lateinit var circleImageView: CircleImageView
    lateinit var buttonSaveChanges: Button
    lateinit var buttonEditImage: Button
    lateinit var buttonEditProfile: Button
    lateinit var username : TextView
    lateinit var course : TextView
    lateinit var address : TextView
    lateinit var email : TextView
    lateinit var studentNumber: TextView
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Get Layout
        binding = FragmentProfileBinding.inflate(layoutInflater)
        buttonSaveChanges = binding.buttonSaveChanges
        buttonEditImage = binding.buttonEditImage
        buttonEditProfile = binding.buttonEditActivity
        circleImageView = binding.circleImageViewLogo
        username = binding.textViewUserName
        course = binding.textViewCourse
        address = binding.textViewAddress
        email = binding.textViewEmail
        studentNumber = binding.textViewStudentNumber

        //Get User By Id
        getCurrentUser()

        //Load Image
        val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            imageUri = it.data?.data
            circleImageView.setImageURI(imageUri)

        }

        buttonSaveChanges.setOnClickListener {
            uploadImageToFirebaseStorage()
        }

        buttonEditImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getImage.launch(intent)
            uploadImageToFirebaseStorage()
        }

        buttonEditProfile.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }


        return binding.root
    }


    private fun uploadImageToFirebaseStorage(){

        if(imageUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(imageUri!!)
            .addOnSuccessListener {

                println("Image uploaded successfully: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {

                    saveUserToFireStore(it.toString())

                }

            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), "Erro a uploadar a imagem!", Toast.LENGTH_SHORT).show()
            }

    }

    private fun saveUserToFireStore(imageURL: String) {

        val uid = FirebaseAuth.getInstance().uid
        val ref = Firebase.firestore.collection("User").document(uid!!)
        val imageMap = hashMapOf<String, Any>(
            "imageURL" to imageURL
        )
        ref.update(imageMap)
    }



    private fun getCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = Firebase.firestore.collection("User").document(uid!!)

        ref.get().addOnSuccessListener { result ->

            val user = result.toObject(User::class.java)

            Picasso.get().load(user!!.imageURL).into(circleImageView)
            username.setText(user.username)
            course.text = user.course
            address.setText(user.address)
            email.setText(user.email)
            studentNumber.setText(user.student_number)

        }
    }


    companion object {
        val FIREBASEURL = "https://messenger-28931-default-rtdb.europe-west1.firebasedatabase.app/"
    }

}