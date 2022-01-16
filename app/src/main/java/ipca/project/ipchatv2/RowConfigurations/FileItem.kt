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
        val textViewFileSize = viewHolder.itemView.textViewFileSize
        val imageViewFile = viewHolder.itemView.imageViewFile
        val imageViewDetails = viewHolder.itemView.imageViewDetails

        textViewFileName.text = file.name

        if(details)
            imageViewDetails.visibility = View.VISIBLE
        else
            imageViewDetails.visibility = View.GONE


        if(file.type == "FOLDER")
        {

            imageViewFile.setImageResource(R.drawable.folder)
            textViewFileSize.text = "${file.subFiles} files"

        }
        else
        {

            imageViewFile.setImageResource(R.drawable.file)
            textViewFileSize.text = "%.2f".format(file.size)

        }
    }

    override fun getLayout(): Int {
        return R.layout.row_file
    }
}