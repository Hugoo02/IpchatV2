package ipca.project.ipchatv2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.FragmentProfileBinding


class ProfileFragment : Fragment(R.layout.fragment_profile)  {

    //Variables
    lateinit var binding: FragmentProfileBinding
    lateinit var circleImageView: CircleImageView
    lateinit var buttonEditProfile: Button
    lateinit var username : TextView
    lateinit var course : TextView
    lateinit var address : TextView
    lateinit var email : TextView
    lateinit var studentNumber: TextView
    lateinit var biography: TextView
    lateinit var settings: ImageButton
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mAuth = FirebaseAuth.getInstance()
        //Get Layout
        binding = FragmentProfileBinding.inflate(layoutInflater)
        buttonEditProfile = binding.buttonEditActivity
        circleImageView = binding.circleImageViewLogo
        username = binding.textViewUserName
        course = binding.textViewCourse
        address = binding.textViewAddress
        email = binding.textViewEmail
        studentNumber = binding.textViewStudentNumber
        biography = binding.textViewBiography
        settings = binding.buttonSettings

        getCurrentUser()

        settings.setOnClickListener() {
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent)
        }

        buttonEditProfile.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }


    private fun getCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = Firebase.firestore.collection("User").document(uid!!)

        ref.addSnapshotListener { value, error ->

            val user = value!!.toObject(User::class.java)

            Picasso.get().load(user!!.imageURL).into(circleImageView)
            username.text = user.username
            course.text = user.course
            address.text = user.address
            email.text = user.email
            studentNumber.text = user.student_number
            biography.text = user.biography

        }
    }

}