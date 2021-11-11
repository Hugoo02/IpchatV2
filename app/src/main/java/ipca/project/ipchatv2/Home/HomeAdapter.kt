package ipca.project.ipchatv2.Home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class HomeAdapter(fm: FragmentManager, private val context: Context, var totalTabs: Int) :
    FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> {
                return UserListFragment()

            }

            1 -> {

                return GroupListFragment()

            }

            else -> {

                return UserListFragment()

            }

        }

    }
}