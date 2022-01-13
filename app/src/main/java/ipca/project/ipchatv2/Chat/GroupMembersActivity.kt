package ipca.project.ipchatv2.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ipca.project.ipchatv2.Home.HomeAdapter
import ipca.project.ipchatv2.Models.GroupChannel
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.ShowUsersActivity
import ipca.project.ipchatv2.databinding.ActivityChatMoreDetailsBinding
import ipca.project.ipchatv2.databinding.ActivityGroupMembersBinding
import java.util.ArrayList

class GroupMembersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupMembersBinding
    val currentUserId = FirebaseAuth.getInstance().uid
    val db = FirebaseFirestore.getInstance()
    var admin : Boolean? = null

    var userIds : MutableList<String>? = null
    var groupId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        groupId = intent.getStringExtra("groupId")

        checkIfAdmin()

        getGroupMembers()

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        tabLayout.addTab(tabLayout.newTab().setText("Todos"))
        tabLayout.addTab(tabLayout.newTab().setText("Admins"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = ShowGroupUsersAdapter(supportFragmentManager , this,
            tabLayout.tabCount, groupId!!)
        viewPager.adapter = adapter


        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

    }

    private fun configPageViews() {

        if(admin!!)
            binding.buttonAddMembers.visibility = View.VISIBLE

        binding.buttonAddMembers.setOnClickListener {

            val intent = Intent(this, ShowUsersActivity::class.java)

            println("userIds = $userIds")

            val bundle = Bundle()
            bundle.putStringArrayList("channelMemberList", userIds as ArrayList<String>)
            bundle.putString("groupId", groupId)

            intent.putExtras(bundle)

            startActivity(intent)

        }

        binding.buttonBack.setOnClickListener {

            finish()

        }

    }

    private fun getGroupMembers() {
        db.collection("groupChannels")
            .document(groupId!!)
            .get()
            .addOnSuccessListener {

                val channel = it.toObject(GroupChannel::class.java)

                userIds = channel!!.userIds

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

                configPageViews()

            }

    }
}