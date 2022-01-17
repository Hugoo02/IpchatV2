package ipca.project.ipchatv2.RowConfigurations

import android.view.View
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.FileModel
import ipca.project.ipchatv2.R
import kotlinx.android.synthetic.main.row_file.view.*

class FileItem(val file: FileModel, val details: Boolean): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewFileName = viewHolder.itemView.textViewFileName
        val imageViewDetails = viewHolder.itemView.imageViewDetails
        val textViewFileExtension = viewHolder.itemView.textViewFileExtension

        textViewFileName.text = file.name

        textViewFileExtension.text = file.extension

        if(details)
            imageViewDetails.visibility = View.VISIBLE
        else
            imageViewDetails.visibility = View.GONE

    }

    override fun getLayout(): Int {
        return R.layout.row_file
    }
}