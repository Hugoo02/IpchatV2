package ipca.project.ipchatv2.RowConfigurations

import android.view.View
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.FileModel
import ipca.project.ipchatv2.R
import kotlinx.android.synthetic.main.row_file.view.*
import kotlinx.android.synthetic.main.row_link.view.*

class LinkItem(val link: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewLink = viewHolder.itemView.textViewLink

        textViewLink.text = link

    }

    override fun getLayout(): Int {
        return R.layout.row_link
    }
}