package ipca.project.ipchatv2

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ipca.project.ipchatv2.Authentication.LoginActivity
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.FragmentProfileBinding
import java.util.*

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
    lateinit var logout: ImageButton
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
        logout = binding.buttonLogout

        getCurrentUser()

        logout.setOnClickListener {

            var builder = AlertDialog.Builder(activity)
            builder.setTitle(getString(R.string.cancel))
            builder.setTitle(getString(R.string.logout))
            builder.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener{ dialog, id ->

                mAuth.signOut()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)

            })
            builder.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener{ dialog, id ->


            })
            var alert = builder.create()
            alert.show()

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