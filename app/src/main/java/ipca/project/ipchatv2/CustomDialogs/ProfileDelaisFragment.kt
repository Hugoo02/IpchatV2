package ipca.project.ipchatv2.CustomDialogs

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso
import ipca.project.ipchatv2.Chat.ChatActivity
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.databinding.FragmentChooseImageBinding
import ipca.project.ipchatv2.databinding.FragmentProfileDelaisBinding

class ProfileDelaisFragment : DialogFragment() {

    private lateinit var binding: FragmentProfileDelaisBinding

    private var userId : String? = null
    private var admin : Boolean? = null
    private var groupId : String? = null

    val currentUserId = FirebaseAuth.getInstance().uid

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("userId")
            admin = it.getBoolean("admin")
            groupId = it.getString("groupId")
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
        val textViewAdmin = binding.textViewAdmin
        val textViewRemove = binding.textViewRemove

        imageButtonExit.setOnClickListener { dismiss() }

        textViewRemove.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.cancel))
            builder.setTitle("Tem a certeza que deseja remover ${textViewUserName.text}?")
            builder.setPositiveButton("Sim", DialogInterface.OnClickListener{ dialog, id ->

                val refGroup = db.collection("groupChannels")
                    .document(groupId!!)

                refGroup.get()
                .addOnSuccessListener { result ->

                    val channel = result.toObject(GroupChannel::class.java)

                    channel!!.userIds!!.remove(userId)

                    refGroup.update("userIds", channel.userIds)

                    db.collection("User")
                        .document(userId!!)
                        .collection("groupChannels")
                        .document(groupId!!)
                        .delete()

                    dismiss()

                }

            })
            builder.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener{ dialog, id ->


            })
            val alert = builder.create()
            alert.show()

        }

        textViewAdmin.setOnClickListener {

            val refUserGroup = db.collection("User")
                .document(userId!!)
                .collection("groupChannels")
                .document(groupId!!)

            if(textViewAdmin.text == "Remover cargo de admininstrador")
            {

                refUserGroup.update("admin", false)
                textViewAdmin.text = "Atribuir cargo de admininstrador"

            }

            else
            {

                refUserGroup.update("admin", true)
                textViewAdmin.text = "Remover cargo de admininstrador"

            }

        }

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


                if(user.id == currentUserId || groupId == null){

                    textViewAdmin.visibility = View.GONE
                    textViewRemove.visibility = View.GONE

                }else if(admin!! && user.id != currentUserId)
                    textViewAdmin.text = "Remover cargo de admininstrador"


            }

        return binding.root
    }
}