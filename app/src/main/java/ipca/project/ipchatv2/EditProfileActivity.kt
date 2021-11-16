package ipca.project.ipchatv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ipca.project.ipchatv2.Models.User
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    lateinit var circleImageView: CircleImageView
    lateinit var buttonGoBack: Button
    lateinit var buttonEditImage: Button
    lateinit var buttonEditProfile: Button
    lateinit var username : EditText
    lateinit var course : TextInputEditText
    lateinit var address : TextInputEditText
    lateinit var email : TextInputEditText
    lateinit var studentNumber: TextInputEditText
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

         buttonGoBack = findViewById<Button>(R.id.buttonEditProfile)
         circleImageView = findViewById<CircleImageView>(R.id.circleImageViewLogo)
         buttonEditImage = findViewById<Button>(R.id.buttonEditImage)
         buttonEditProfile = findViewById<Button>(R.id.buttonEditProfile)
         username = findViewById<EditText>(R.id.EditTextName)
         course  = findViewById<TextInputEditText>(R.id.textInputEditTextCourse)
         address = findViewById<TextInputEditText>(R.id.textInputEditTextAdress)
         email = findViewById<TextInputEditText>(R.id.textInputEditTextEmail)
         studentNumber = findViewById<TextInputEditText>(R.id.textInputEditTextStudentNumber)


        getCurrentUser()

        val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            imageUri = it.data?.data
            circleImageView.setImageURI(imageUri)

        }

        buttonEditProfile.setOnClickListener {

            editCurrentUser()
        }

        buttonEditImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getImage.launch(intent)
            uploadImageToFirebaseStorage()
        }

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
                Toast.makeText(this, "Erro a uploadar a imagem!", Toast.LENGTH_SHORT).show()
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
            course.setText(user.course)
            address.setText(user.address)
            email.setText(user.email)
            studentNumber.setText(user.student_number)

        }
    }

    private fun editCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = Firebase.firestore.collection("User").document(uid!!)

        ref.get().addOnSuccessListener { result ->

            val user = result.toObject(User::class.java)

            var userTest = hashMapOf(
                "username" to username.text.toString()
            )

            Firebase.firestore.collection("User").document(uid!!).set(userTest, SetOptions.merge())


            /*Picasso.get().load(user!!.imageURL).into(circleImageView)
            user!!.username
            user!!.course = course.toString()
            user!!.email = email.toString()
            user!!.student_number = studentNumber.toString()
            user!!.id = user.id
            user!!.year = user.year
            user!!.gender = user.gender*/

        }
    }
}