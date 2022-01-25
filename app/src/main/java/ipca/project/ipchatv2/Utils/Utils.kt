package ipca.project.ipchatv2.Utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import androidx.lifecycle.LifecycleOwner
import ipca.project.ipchatv2.Models.MessageGroup
import ipca.project.ipchatv2.R
import java.lang.String.format
import java.text.DecimalFormat
import java.util.*
import kotlin.math.min
import kotlin.system.exitProcess

object Utils {

    fun connectionLiveData(context: Context) {

        var alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Sem internet")
        alertDialog.setMessage("A aplicação precisa de acesso à internet para funcionar corretamente. Ligue os dados móveis ou o wifi para continuar.")
        alertDialog.setIcon(R.drawable.ic_sem_wi_fi)

        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.setOnCancelListener {
            //moveTaskToBack(true)
            exitProcess(-1)
        }
        //alert.setCancelable(false)

        var connectionLiveData = ConnectionLiveData(context)
        connectionLiveData.observe(context as LifecycleOwner, { isNetworkAvailable ->
            if(!isNetworkAvailable) {
                alert.show()
            } else {
                alert.dismiss()
            }
        })
    }

    fun formatDateToChat(date: Date) : String {

        val calendar = Calendar.getInstance()


        calendar.time = date


        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute =  calendar.get(Calendar.MINUTE)

        val formatter = DecimalFormat("00")
        val formattedHour = formatter.format(hour)
        val formattedMinute = formatter.format(minute)

        return "$formattedHour:$formattedMinute"

    }

    fun receiveDateFromDatabaseToCalendar(date: Date): String {

        val calendar = Calendar.getInstance()
        calendar.time = date

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val formatter = DecimalFormat("00")
        val formattedDay = formatter.format(day)
        val formattedMonth = formatter.format(month)

        return "$formattedDay/$formattedMonth/$year"
    }

    fun dateToCalenar(time: Date): Calendar{

        val calendar = Calendar.getInstance()
        calendar.time = time

        return calendar

    }

    fun getMessageDate(time: Date) : String{

        val currentCalenar = Calendar.getInstance()
        val messageCalendar = Calendar.getInstance()
        messageCalendar.time = time

        val messageYear = messageCalendar.get(Calendar.YEAR)
        val messageMonth = messageCalendar.get(Calendar.MONTH)
        val messageDay = messageCalendar.get(Calendar.DAY_OF_MONTH)

        val currentYear = currentCalenar.get(Calendar.YEAR)
        val currentMonth = currentCalenar.get(Calendar.MONTH)
        val currentDay = currentCalenar.get(Calendar.DAY_OF_MONTH)

        if(currentYear == messageYear && currentMonth == messageMonth
            && currentDay == messageDay)
            //return "Hoje às ${formatDateToChat(time)}"
            return "Hoje às ${formatDateToChat(time)}"
        else{

            val formatter = DecimalFormat("00")
            val formattedDay = formatter.format(messageDay)
            val formattedMonth = formatter.format(messageMonth + 1)

            return "${formattedDay}/${formattedMonth}/${messageYear} ${formatDateToChat(time)}"
        }
           

    }

    fun filterAdapter(queryText: String, array: HashMap<String, MessageGroup>){



    }

}