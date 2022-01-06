package ipca.project.ipchatv2.Utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import androidx.lifecycle.LifecycleOwner
import ipca.project.ipchatv2.R
import java.util.*
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
        val minute = calendar.get(Calendar.MINUTE)

        return "$hour:$minute"

    }

    fun formatDateToFistChat(date: Date) : String {

        val calendar = Calendar.getInstance()

        calendar.time = date

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return "$day/$month/$year"

    }

    fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap
                .height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

}