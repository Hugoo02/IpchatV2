package ipca.project.ipchatv2


import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import ipca.project.ipchatv2.Home.HomeAdapter
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.databinding.ActivityMainBinding
import ipca.project.ipchatv2.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.row_users.view.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    //val adapter = GroupAdapter<ViewHolder>()

    val latestMessageMap = HashMap<String, ChatMessage>()

    /*
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
        }
    }

     */

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

        tabLayout.addTab(tabLayout.newTab().setText("Pessoas"))
        tabLayout.addTab(tabLayout.newTab().setText("Grupos"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

       // val adapter = HomeAdapter(getFragmentManager()!! , requireContext(), tabLayout.tabCount)
       // viewPager.adapter = adapter


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

        binding.buttonNewMessage.setOnClickListener {

            val intent = Intent(activity, ShowUsersActivity::class.java)
            startActivity(intent)

        }

        return binding.root
    }

    /*
    fun openSomeActivityForResult() {
        val intent = Intent(requireContext(), ShowUsersActivity::class.java)
        resultLauncher!!.launch(intent)
    }*/

}

/*
class ChatMessage(val user: User, val chatMessage: ChatMessage): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewUserName = viewHolder.itemView.textViewUserName
        val circleImagePhoto = viewHolder.itemView.circleImagePhoto

        textViewUserName.text = user.username


        Picasso.get().load(user.imageURL).resize(100, 100).centerCrop()
            .into(circleImagePhoto)
    }

    override fun getLayout(): Int {
        return R.layout.row_users
    }
}

 */