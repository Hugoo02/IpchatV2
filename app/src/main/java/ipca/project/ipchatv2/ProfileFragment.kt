package ipca.project.ipchatv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.FragmentProfileBinding
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentProfileBinding
    lateinit var circleImageView: CircleImageView
    lateinit var username : TextInputEditText
    lateinit var course : TextInputEditText
    lateinit var address : TextInputEditText
    lateinit var email : TextInputEditText
    lateinit var studentNumber: TextInputEditText
    lateinit var buttonEditProfile: Button
    private val pickImage = 100
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        circleImageView = binding.circleImageViewLogo
        username = binding.textInputEditTextName
        course = binding.textInputEditTextCourse
        address = binding.textInputEditTextAdress
        email = binding.textInputEditTextEmail
        studentNumber = binding.textInputEditTextStudentNumber
        buttonEditProfile = binding.buttonEditProfile

        getCurrentUser()

        buttonEditProfile.setOnClickListener {

        }

        val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            imageUri = it.data?.data
            circleImageView.setImageURI(imageUri)

        }


        return binding.root
    }


    private fun getCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = Firebase.firestore.collection("User").document(uid!!)

        ref.get().addOnSuccessListener { result ->

            val user = result.toObject(User::class.java)

            Picasso.get().load(user!!.imageURL).into(circleImageView)
            username.setText(user.username)
            course.setText(user.course)
            address.setText(user.address)
            email.setText(user.email)
            studentNumber.setText(user.student_number)

        }
    }


    companion object {
        val FIREBASEURL = "https://messenger-28931-default-rtdb.europe-west1.firebasedatabase.app/"
    }
}