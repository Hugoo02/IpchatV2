package ipca.project.ipchatv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ipca.project.ipchatv2.Models.User
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().uid

    lateinit var circleImageView: CircleImageView
    lateinit var buttonGoBack: Button
    lateinit var settings: ImageButton
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

        settings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        supportActionBar!!.hide()

         buttonGoBack = findViewById(R.id.buttonEditProfile)
         circleImageView = findViewById(R.id.circleImageViewLogo)
         buttonEditImage = findViewById(R.id.buttonEditImage)
         buttonEditProfile = findViewById(R.id.buttonEditProfile)
         username = findViewById(R.id.textViewUserName)
         course  = findViewById(R.id.textViewCourse)
         address = findViewById(R.id.editTextAddress)
         email = findViewById(R.id.textViewEmail)
         studentNumber = findViewById(R.id.textViewStudentNumber)
         biography = findViewById(R.id.editTextBiography)



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

        db.collection("User")
            .document(currentUserId!!)
            .get()
            .addOnSuccessListener { result ->

                val user = result.toObject(User::class.java)

                val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(user!!.imageURL!!)

                imageRef.delete()

            }

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

        val ref = Firebase.firestore.collection("User").document(currentUserId!!)

        ref.get().addOnSuccessListener { result ->

            val user = result.toObject(User::class.java)

            var userEdit = hashMapOf(
                "biography" to biography.text.toString(),
                "address" to address.text.toString()
            )

            Firebase.firestore.collection("User").document(currentUserId!!).set(userEdit, SetOptions.merge())

        }
    }
}