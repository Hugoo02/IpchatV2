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
        val imageViewFile = viewHolder.itemView.imageViewFile

        textViewFileName.text = file.name

        textViewFileExtension.text = file.extension

        when(file.extension)
        {
            "pdf" -> {

                imageViewFile.setImageResource(R.drawable.pdf)

            }

            "docx" -> {

                imageViewFile.setImageResource(R.drawable.word)

            }

            else -> {

                imageViewFile.setImageResource(R.drawable.file)

            }

        }

        if(details)
            imageViewDetails.visibility = View.VISIBLE
        else
            imageViewDetails.visibility = View.GONE

    }

    override fun getLayout(): Int {
        return R.layout.row_file
    }
}