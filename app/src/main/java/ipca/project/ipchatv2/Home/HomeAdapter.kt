package ipca.project.ipchatv2.Home

import android.content.Context
import androidx.fragment.app.*


class HomeAdapter(fm: FragmentManager, private val context: Context, var totalTabs: Int) :
    FragmentStatePagerAdapter(fm) {

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