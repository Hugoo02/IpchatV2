package ipca.project.ipchatv2


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import ipca.project.ipchatv2.Home.HomeAdapter
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.Notifications.FirebaseService
import ipca.project.ipchatv2.Utils.Utils
import ipca.project.ipchatv2.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Utils.connectionLiveData(requireContext())


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        //tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.tab_layout_person))
        tabLayout.addTab(tabLayout.newTab().setText("Privados"))
        tabLayout.addTab(tabLayout.newTab().setText("Grupos"))
        //tabLayout.setBackgroundResource()
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = HomeAdapter(requireFragmentManager() , requireContext(), tabLayout.tabCount)
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


        return binding.root
    }
}