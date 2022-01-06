package ipca.project.ipchatv2.Chat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.*


class ShowFilesAdapter(fm: FragmentManager, private val context: Context, var totalTabs: Int,
                       val groupId : String, val channelType: String) :
    FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> {

                val ShowPhotosFragment = ShowPhotosFragment()
                val bundle = Bundle()

                bundle.putString("groupId", groupId)
                bundle.putString("channelType", channelType)

                ShowPhotosFragment.arguments = bundle

                return ShowPhotosFragment

            }

            1 -> {

                val showDocsFragment = ShowDocsFragment()
                val bundle = Bundle()

                bundle.putString("groupId", groupId)
                bundle.putString("channelType", channelType)

                showDocsFragment.arguments = bundle

                return showDocsFragment

            }

            2-> {

                val showLinksFragment = ShowLinksFragment()
                val bundle = Bundle()

                bundle.putString("groupId", groupId)
                bundle.putString("channelType", channelType)

                showLinksFragment.arguments = bundle

                return showLinksFragment

            }

            else -> {

                val ShowPhotosFragment = ShowPhotosFragment()
                val bundle = Bundle()

                bundle.putString("groupId", groupId)
                bundle.putString("channelType", channelType)

                ShowPhotosFragment.arguments = bundle

                return ShowPhotosFragment

            }

        }

    }
}