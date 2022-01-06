package ipca.project.ipchatv2.RowConfigurations

import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.R
import kotlinx.android.synthetic.main.row_show_image.view.*
import android.graphics.PorterDuffXfermode

import android.graphics.RectF

import android.graphics.Bitmap
import ipca.project.ipchatv2.Utils.Utils


class PhotoItem(val message: ChatMessage): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val imageViewPhoto = viewHolder.itemView.imageViewPhoto

        Picasso.get().load(message.text).resize(250, 250).centerCrop().into(imageViewPhoto)

    }

    override fun getLayout(): Int {
        return R.layout.row_show_image
    }

    override fun getSpanSize(spanCount: Int, position: Int) = spanCount / 3
}