package ipca.project.ipchatv2.Calendar

import android.widget.TextView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import ipca.project.ipchatv2.Models.Calendar
import ipca.project.ipchatv2.R
import ipca.project.ipchatv2.Utils

class CalendarRow(val calendar: Calendar): Item<ViewHolder>() {


    override fun bind(viewHolder: ViewHolder, position: Int) {

        val textViewDate = viewHolder.itemView.findViewById<TextView>(R.id.textViewDate)
        val textViewName = viewHolder.itemView.findViewById<TextView>(R.id.textViewName)
        val textViewDescription = viewHolder.itemView.findViewById<TextView>(R.id.textViewDescription)

        textViewDate.text = Utils.receiveDateFromDatabaseToCalendar(calendar.date!!)
        textViewName.text = calendar.name
        textViewDescription.text = calendar.description
    }

    override fun getLayout(): Int {
        return R.layout.row_calendar
    }
}

