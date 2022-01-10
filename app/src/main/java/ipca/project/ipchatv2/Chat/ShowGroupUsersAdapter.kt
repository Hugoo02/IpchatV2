package ipca.project.ipchatv2.Chat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.*


class ShowGroupUsersAdapter(fm: FragmentManager, private val context: Context, var totalTabs: Int,
                            val groupId : String) :
    FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> {

                val showGroupUsersFragment = ShowGroupUsersFragment()
                val bundle = Bundle()

                bundle.putString("groupId", groupId)
                bundle.putString("userType", "All")

                showGroupUsersFragment.arguments = bundle

                return showGroupUsersFragment

            }

            1 -> {

                val showGroupUsersFragment = ShowGroupUsersFragment()
                val bundle = Bundle()

                bundle.putString("groupId", groupId)
                bundle.putString("userType", "Admin")

                showGroupUsersFragment.arguments = bundle

                return showGroupUsersFragment

            }

            else -> {

                val showGroupUsersFragment = ShowGroupUsersFragment()
                val bundle = Bundle()

                bundle.putString("groupId", groupId)
                bundle.putString("userType", "All")

                showGroupUsersFragment.arguments = bundle

                return showGroupUsersFragment

            }

        }

    }
}