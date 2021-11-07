package ipca.project.ipchatv2

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.LifecycleOwner
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

}