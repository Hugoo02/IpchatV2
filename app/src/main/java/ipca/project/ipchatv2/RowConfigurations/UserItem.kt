package ipca.project.ipchatv2.RowConfigurations

import android.view.View
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import kotlinx.android.synthetic.main.row_users.view.*

class UserItem(val user: User, val admin: Boolean): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewUserName = viewHolder.itemView.textViewUserName
        val circleImagePhoto = viewHolder.itemView.circleImagePhoto
        val textViewAdmin = viewHolder.itemView.textViewAdmin

        println("passou na configuração da row")

        if(admin)
            textViewAdmin.visibility = View.VISIBLE
        else
            textViewAdmin.visibility = View.INVISIBLE

        textViewUserName.text = user.username

        if(viewHolder.itemView.isSelected)
        {

            viewHolder.itemView.circleImagePhoto.setImageResource(R.drawable.selected)

        }
        else
        {

            Picasso.get().load(user.imageURL).resize(100, 100).centerCrop().into(circleImagePhoto)

        }


    }

    override fun getLayout(): Int {
        return R.layout.row_users
    }
}