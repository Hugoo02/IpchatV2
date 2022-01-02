package ipca.project.ipchatv2.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import ipca.project.ipchatv2.databinding.ActivityGroupMembersBinding
import ipca.project.ipchatv2.databinding.ActivityShowFilesBinding

class ShowFilesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowFilesBinding

    var groupId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        groupId = intent.getStringExtra("groupId")

        binding.buttonBack.setOnClickListener {

            finish()

        }

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
}