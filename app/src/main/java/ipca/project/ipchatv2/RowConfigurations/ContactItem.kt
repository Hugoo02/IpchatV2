package ipca.project.ipchatv2.RowConfigurations

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.ContactModel
import ipca.project.ipchatv2.R
import kotlinx.android.synthetic.main.row_contact.view.*

class ContactItem(val contact: ContactModel): Item<ViewHolder>() {

    val db = FirebaseFirestore.getInstance()

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val imageViewMoreDetails = viewHolder.itemView.imageViewMoreDetails
        val textViewContactName = viewHolder.itemView.textViewContactName
        val textViewSchoolName = viewHolder.itemView.textViewSchoolName
        val textViewAddress = viewHolder.itemView.textViewAddress
        val textViewCity = viewHolder.itemView.textViewCity
        val textViewPhone = viewHolder.itemView.textViewPhone
        val textViewEmail = viewHolder.itemView.textViewEmail

        textViewContactName.text = contact.name
        textViewSchoolName.text = contact.schoolName
        textViewAddress.text = contact.address
        textViewCity.text = contact.city
        textViewPhone.text = contact.phoneNumber
        textViewEmail.text = contact.email

        imageViewMoreDetails.setOnClickListener {

            if(textViewEmail.visibility == View.GONE){

                imageViewMoreDetails.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
                textViewSchoolName.visibility = View.VISIBLE
                textViewAddress.visibility = View.VISIBLE
                textViewCity.visibility = View.VISIBLE
                textViewPhone.visibility = View.VISIBLE
                textViewEmail.visibility = View.VISIBLE

                val constraintLayout = viewHolder.itemView.findViewById<ConstraintLayout>(R.id.constraintLayout)
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.clear(
                    R.id.textViewContactName,
                    ConstraintSet.BOTTOM
                )

                constraintSet.applyTo(constraintLayout)

            }else{

                imageViewMoreDetails.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
                textViewSchoolName.visibility = View.GONE
                textViewAddress.visibility = View.GONE
                textViewCity.visibility = View.GONE
                textViewPhone.visibility = View.GONE
                textViewEmail.visibility = View.GONE

            }



        }

    }

    override fun getLayout(): Int {
        return R.layout.row_contact
    }
}