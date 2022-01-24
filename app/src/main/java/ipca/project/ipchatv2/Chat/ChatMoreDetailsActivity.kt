package ipca.project.ipchatv2.Chat

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso
import ipca.project.ipchatv2.Authentication.LoginActivity
import ipca.project.ipchatv2.MainActivity
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.Models.PrivateChannel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.databinding.ActivityChatBinding
import ipca.project.ipchatv2.databinding.ActivityChatMoreDetailsBinding
import ipca.project.ipchatv2.databinding.ActivityCreateNewGroupBinding

class ChatMoreDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatMoreDetailsBinding
    val currentUserId = FirebaseAuth.getInstance().uid

    var groupId : String? = null
    var channelType: String? = null
    var admin : Boolean? = null

    val adminList : MutableList<String> = arrayListOf()

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMoreDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        groupId = intent.getStringExtra("groupId")
        channelType = intent.getStringExtra("channelType")

        if(channelType == "group")
            checkIfAdmin()

        configureActivity()

        if(channelType == "private"){

            binding.textViewGroupInformation.visibility = View.GONE
            binding.textViewGroupMembers.visibility = View.GONE
            binding.textViewLeaveGroup.visibility = View.GONE
            binding.imageViewLeaveGroup.visibility = View.GONE
            binding.textViewRemoveGroup.visibility = View.GONE
            binding.imageViewRemoveGroup.visibility = View.GONE
            binding.textViewChangeGroupName.visibility = View.GONE
            binding.textViewChangeGroupImage.visibility = View.GONE

        }

        binding.buttonBack.setOnClickListener {

            finish()

        }

        binding.textViewGroupMembers.setOnClickListener {

            val intent = Intent(this, GroupMembersActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("admin", admin)
            startActivity(intent)

        }

        binding.textViewFiles.setOnClickListener {

            val intent = Intent(this, ShowFilesActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("channelType", channelType)
            startActivity(intent)

        }

        binding.textViewLeaveGroup.setOnClickListener {

            adminList()

            adminList.forEach {

                println("admin = $it")
            }

            var builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.cancel))
            builder.setTitle("Tem a certeza que deseja sair do grupo?")
            builder.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener{ dialog, id ->

                leftGroup()

            })
            builder.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener{ dialog, id ->


            })
            var alert = builder.create()
            alert.show()

        }

        binding.textViewRemoveGroup.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.cancel))
            builder.setTitle("Tem a certeza que deseja sair do grupo?")
            builder.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener{ dialog, id ->

                removeGroup()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            })
            builder.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener{ dialog, id ->


            })
            val alert = builder.create()
            alert.show()

        }

        binding.textViewChangeGroupName.setOnClickListener {

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_change_group_name)

            val editTextName = dialog.findViewById<EditText>(R.id.editTextName)
            val buttonCancel = dialog.findViewById<Button>(R.id.buttonCancel)
            val buttonConfirm = dialog.findViewById<Button>(R.id.buttonConfirm)

            buttonCancel.setOnClickListener {

                dialog.dismiss()

            }

            buttonConfirm.setOnClickListener {

                val name = editTextName.text.toString()

                if(name.isBlank()){

                    Toast.makeText(this, "Nome vazio!", Toast.LENGTH_SHORT).show()

                }else{

                    db.collection("groupChannels")
                        .document(groupId!!)
                        .update(mapOf("chatName" to editTextName.text.toString())).addOnSuccessListener {

                            dialog.dismiss()

                        }

                }

            }

            dialog.show()
            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setGravity(Gravity.CENTER)

        }

    }

    private fun adminList() {

        val refGroup = db.collection("groupChannels")
            .document(groupId!!)

        refGroup.get()
            .addOnSuccessListener {

                adminList.clear()

                val channel = it.toObject(GroupChannel::class.java)

                val userList = channel!!.userIds

                //Check if there is more admins
                userList!!.forEachIndexed { index, id ->

                    db.collection("User")
                        .document(id)
                        .collection("groupChannels")
                        .document(groupId!!)
                        .get()
                        .addOnSuccessListener { result ->

                            val admin = result.getBoolean("admin")

                            if (admin!!) {
                                adminList.add(id)
                            }

                        }

                    }
            }
    }

    private fun leftGroup() {

        val refGroup = db.collection("groupChannels")
            .document(groupId!!)

        refGroup.get()
            .addOnSuccessListener {

                val channel = it.toObject(GroupChannel::class.java)

                val userList = channel!!.userIds

                //Se não for admin ou se existirem mais admins no grupo
                if(!admin!! || adminList.size >= 2)
                {

                    userList!!.forEachIndexed { index, id ->

                        if(id == currentUserId)
                            userList.removeAt(index)

                    }

                    db.collection("User")
                        .document(currentUserId!!)
                        .collection("groupChannels")
                        .document(groupId!!)
                        .delete()

                    refGroup.update(mapOf("userIds" to userList))

                    removeGroupEvents("individual")

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }else{

                    var builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.cancel))
                    builder.setTitle("Se sair o grupo será apagado. Deseja continuar?")
                    builder.setPositiveButton(getString(R.string.yes), DialogInterface.OnClickListener{ dialog, id ->

                        removeGroup()

                        removeGroupEvents("group")

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()


                    })
                    builder.setNegativeButton(getString(R.string.no), DialogInterface.OnClickListener{ dialog, id ->


                    })
                    var alert = builder.create()
                    alert.show()

                }

            }

    }

    private fun removeGroup() {

        val refGroup = db.collection("groupChannels")
            .document(groupId!!)

        refGroup.get()
            .addOnSuccessListener {

                val channel = it.toObject(GroupChannel::class.java)

                val userList = channel!!.userIds

                removeGroupEvents("group")

                userList!!.forEachIndexed { index, id ->

                    db.collection("User")
                        .document(id)
                        .collection("groupChannels")
                        .document(groupId!!)
                        .delete()
                        .addOnSuccessListener {

                            if(index == (userList.size - 1))
                            {

                                refGroup.collection("lastMessage").get().addOnSuccessListener {

                                    for(doc in it.documents)
                                        refGroup.collection("lastMessage").document(doc.id).delete()

                                }

                                refGroup.collection("messages").get().addOnSuccessListener {

                                    for(doc in it.documents)
                                        refGroup.collection("messages").document(doc.id).delete()

                                }
                                refGroup.delete()

                            }

                        }

                }

            }

    }

    private fun removeGroupEvents(removeType: String) {

        //Ainda não está a funcionar
        if (removeType == "individual")
        {

            val userCalendarRef = db.collection("Calendar")
                .document(currentUserId!!)
                .collection("Meetings")

            val groupCalendarRef = db.collection("Calendar")
                .document(groupId!!)
                .collection("Meetings")

            userCalendarRef.get()
                .addOnSuccessListener { userCalendar ->

                    groupCalendarRef.get().addOnSuccessListener { groupCalendar ->

                        for(groupDoc in groupCalendar.documents){

                            for(userDoc in userCalendar.documents){

                                if(userDoc.id == groupDoc.id)
                                {

                                    userCalendarRef.document(userDoc.id).delete()

                                }

                            }

                        }

                    }

                }

        }else{

            val refGroup = db.collection("groupChannels")
                .document(groupId!!)

            refGroup.get().addOnSuccessListener { result ->

                val channel = result.toObject(GroupChannel::class.java)

                val groupCalendarRef = db.collection("Calendar")
                    .document(groupId!!)
                    .collection("Meetings")

                val userList = channel!!.userIds

                userList!!.forEachIndexed { index, id ->

                    val userCalendarRef = db.collection("Calendar")
                        .document(id)
                        .collection("Meetings")

                    userCalendarRef.get()
                        .addOnSuccessListener { userCalendar ->

                            groupCalendarRef.get().addOnSuccessListener { groupCalendar ->

                                for(groupDoc in groupCalendar.documents){

                                    for(userDoc in userCalendar.documents){

                                        if(userDoc.id == groupDoc.id)
                                        {

                                            userCalendarRef.document(userDoc.id).delete()

                                        }

                                    }

                                    groupCalendarRef.document(groupDoc.id).delete()

                                }

                            }

                        }

                }

            }


        }


    }

    private fun checkIfAdmin() {

        db.collection("User")
            .document(currentUserId!!)
            .collection("groupChannels")
            .document(groupId!!)
            .get()
            .addOnSuccessListener {

                admin = it.getBoolean("admin")

                if(admin!!)
                    adminList()

            }

    }

    private fun configureActivity() {

        if(channelType == "group")
        {

            db.collection("groupChannels")
                .document(groupId!!)
                .get().addOnSuccessListener { result ->

                    val group = result.toObject(GroupChannel::class.java)

                    val circleImageViewGroup = binding.circleImageViewGroup
                    val textViewGroupName = binding.textViewGroupName

                    textViewGroupName.text = group!!.chatName

                    Picasso.get().load(group.groupImageURL).into(circleImageViewGroup)

                }

        }else{

            db.collection("privateChannels")
                .document(groupId!!)
                .get().addOnSuccessListener { result ->

                    val chat = result.toObject(PrivateChannel::class.java)

                    val circleImageViewGroup = binding.circleImageViewGroup
                    val textViewGroupName = binding.textViewGroupName

                    var guestId : String? = null

                    chat!!.userIds!!.forEach {

                        if(it != currentUserId)
                            guestId = it

                    }

                    db.collection("User")
                        .document(guestId!!)
                        .get()
                        .addOnSuccessListener { userObject ->

                            val user = userObject.toObject(User::class.java)

                            textViewGroupName.text = user!!.username

                            Picasso.get().load(user.imageURL).into(circleImageViewGroup)

                        }

                }

        }

    }
}