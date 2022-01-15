package ipca.project.ipchatv2.RowConfigurations

import android.content.Context
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ChatMessage
import ipca.project.ipchatv2.R
import kotlinx.android.synthetic.main.row_show_image.view.*
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.content.contentValuesOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import ipca.project.ipchatv2.Utils.Utils
import okhttp3.internal.Util


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