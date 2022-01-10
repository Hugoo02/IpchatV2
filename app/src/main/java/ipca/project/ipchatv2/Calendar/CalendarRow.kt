package ipca.project.ipchatv2.Calendar

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.firestore.FirebaseFirestore
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.CalendarModel
import ipca.project.ipchatv2.Models.User
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.Utils.Utils

class CalendarRow(val calendar: CalendarModel): Item<ViewHolder>() {


    override fun bind(viewHolder: ViewHolder, position: Int) {

        val db = FirebaseFirestore.getInstance()

        val textViewDate = viewHolder.itemView.findViewById<TextView>(R.id.textViewDate)
        val textViewTitle = viewHolder.itemView.findViewById<TextView>(R.id.textViewTitle)
        val textViewLocal = viewHolder.itemView.findViewById<TextView>(R.id.textViewLocal)
        val imageViewMap = viewHolder.itemView.findViewById<ImageView>(R.id.imageViewMap)
        val linearLayout = viewHolder.itemView.findViewById<LinearLayout>(R.id.linearLayout)
        val textViewCreatedByText = viewHolder.itemView.findViewById<TextView>(R.id.textViewCreatedByText)
        val textViewCreatedBy = viewHolder.itemView.findViewById<TextView>(R.id.textViewCreatedBy)
        val textViewDescription = viewHolder.itemView.findViewById<TextView>(R.id.textViewDescription)
        val imageButtonMoreDetails = viewHolder.itemView.findViewById<ImageButton>(R.id.imageButtonMoreDetails)

        textViewDate.text = Utils.receiveDateFromDatabaseToCalendar(calendar.date!!)
        textViewTitle.text = calendar.title
        textViewDescription.text = calendar.description

        imageButtonMoreDetails.setOnClickListener {

            if(linearLayout.visibility == View.GONE){

                val constraintDescriptions = viewHolder.itemView.findViewById<ConstraintLayout>(R.id.constraintDescriptions)
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintDescriptions)
                constraintSet.clear(
                    R.id.textView1,
                    ConstraintSet.BOTTOM
                )

                constraintSet.connect(
                    R.id.textViewDescription,
                    ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.END
                )

                textViewDescription.maxLines = 100

                constraintSet.applyTo(constraintDescriptions)

                textViewLocal.visibility = View.VISIBLE
                imageViewMap.visibility = View.VISIBLE
                linearLayout.visibility = View.VISIBLE

                textViewLocal.text = calendar.local

                db.collection("User")
                    .document(calendar.createdBy!!)
                    .get()
                    .addOnSuccessListener { result ->

                        val user = result.toObject(User::class.java)

                        textViewCreatedBy.text = user!!.username

                    }

                imageButtonMoreDetails.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)

            }else{

                val constraintDescriptions = viewHolder.itemView.findViewById<ConstraintLayout>(R.id.constraintDescriptions)
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintDescriptions)
                constraintSet.connect(
                    R.id.textView1,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM,
                    45
                )

                constraintSet.connect(
                    R.id.textViewDescription,
                    ConstraintSet.END,
                    R.id.imageButtonMoreDetails,
                    ConstraintSet.START,
                    5
                )

                textViewDescription.maxLines = 100

                constraintSet.applyTo(constraintDescriptions)

                textViewDescription.maxLines = 1

                textViewLocal.visibility = View.GONE
                imageViewMap.visibility = View.GONE
                linearLayout.visibility = View.GONE

                imageButtonMoreDetails.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)

            }

        }

    }

    override fun getLayout(): Int {
        return R.layout.row_calendar
    }
}

