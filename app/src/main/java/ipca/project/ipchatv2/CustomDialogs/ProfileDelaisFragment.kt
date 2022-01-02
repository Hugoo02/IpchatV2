package ipca.project.ipchatv2.CustomDialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.databinding.FragmentChooseImageBinding
import ipca.project.ipchatv2.databinding.FragmentProfileDelaisBinding

class ProfileDelaisFragment : DialogFragment() {

    private lateinit var binding: FragmentProfileDelaisBinding

    private var userId : String? = null

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("userId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentProfileDelaisBinding.inflate(layoutInflater)

        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imageButtonExit = binding.imageButtonExit
        val circleImagePhoto = binding.circleImagePhoto
        val textViewUserName = binding.textViewUserName
        val textViewCourse =binding.textViewCourse
        val textViewEmail = binding.textViewEmail
        val textViewGrade = binding.textViewGrade
        val textViewStudentNumber = binding.textViewStudentNumber

        imageButtonExit.setOnClickListener { dismiss() }

        db.collection("User")
            .document(userId!!)
            .get()
            .addOnSuccessListener { result->

                val user = result.toObject(User::class.java)

                Picasso.get().load(user!!.imageURL).into(circleImagePhoto)

                textViewUserName.text = user.username
                textViewCourse.text = user.course
                textViewEmail.text = user.email
                textViewGrade.text = user.address
                textViewStudentNumber.text = user.student_number

            }

        return binding.root
    }
}