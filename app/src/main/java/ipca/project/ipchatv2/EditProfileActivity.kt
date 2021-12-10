package ipca.project.ipchatv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
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
    lateinit var buttonEditImage: ImageButton
    lateinit var buttonEditProfile: Button
    lateinit var username : TextView
    lateinit var course : TextView
    lateinit var address : EditText
    lateinit var email : TextView
    lateinit var studentNumber: TextView
    lateinit var biography: EditText
    private var imageUri: Uri? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        mAuth = FirebaseAuth.getInstance()

        if(mAuth == FirebaseAuth.getInstance()){

            getCurrentUser()
        }
        else{
        }

        supportActionBar!!.hide()

         buttonGoBack = findViewById<Button>(R.id.buttonEditProfile)
         circleImageView = findViewById<CircleImageView>(R.id.circleImageViewLogo)
         buttonEditImage = findViewById<ImageButton>(R.id.buttonEditImage)
         buttonEditProfile = findViewById<Button>(R.id.buttonEditProfile)
         username = findViewById<TextView>(R.id.textViewUserName)
         course  = findViewById<TextView>(R.id.textViewCourse)
         address = findViewById<EditText>(R.id.editTextAddress)
         email = findViewById<TextView>(R.id.textViewEmail)
         studentNumber = findViewById<TextView>(R.id.textViewStudentNumber)
         biography = findViewById<EditText>(R.id.editTextBiography)



        val getImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            imageUri = it.data?.data
            circleImageView.setImageURI(imageUri)

        }

        buttonEditProfile.setOnClickListener {

            editCurrentUser()
            uploadImageToFirebaseStorage()
            Toast.makeText(this, "Perfil editado com sucesso!", Toast.LENGTH_SHORT).show()

        }

        buttonEditImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            getImage.launch(intent)
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

                    println("Image chegou la")
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
            biography.setText(user.biography)

        }
    }

    private fun editCurrentUser(){

        val uid = FirebaseAuth.getInstance().uid
        val ref = Firebase.firestore.collection("User").document(uid!!)

        ref.get().addOnSuccessListener { result ->

            val user = result.toObject(User::class.java)

            var userEdit = hashMapOf(
                "biography" to biography.text.toString(),
                "address" to address.text.toString()
            )

            Firebase.firestore.collection("User").document(uid!!).set(userEdit, SetOptions.merge())


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