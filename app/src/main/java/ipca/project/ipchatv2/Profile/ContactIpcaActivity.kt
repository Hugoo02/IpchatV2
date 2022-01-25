package ipca.project.ipchatv2.Profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ContactModel
import ipca.project.ipchatv2.RowConfigurations.ContactItem
import ipca.project.ipchatv2.databinding.ActivityContactIpcaBinding

class ContactIpcaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactIpcaBinding
    val adapter = GroupAdapter<ViewHolder>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     binding = ActivityContactIpcaBinding.inflate(layoutInflater)
     setContentView(binding.root)

        supportActionBar?.hide()

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.recyclerViewContact.adapter = adapter

        getContactList()


    }

    private fun getContactList() {

        db.collection("Contact")
            .get()
            .addOnSuccessListener { result ->

                for(doc in result.documents){

                    val contact = doc.toObject(ContactModel::class.java)

                    adapter.add(ContactItem(contact!!))

                }

            }

    }

}