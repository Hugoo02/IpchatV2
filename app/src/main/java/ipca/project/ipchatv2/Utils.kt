package ipca.project.ipchatv2

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
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

    fun getOrCreatePrivateChannel(otherUserId: String) : String {

        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().uid
        var channelId : String? = null

        val refUserGroups = db.collection("User")
            .document(currentUserId!!)
            .collection("chatChannels")
            .document(otherUserId).get().addOnSuccessListener {

                if(it.exists()){

                    channelId = it["channelId"].toString()

                }
                else{



                }

            }

        return ""

    }


}