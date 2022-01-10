package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class PrivateChannel (
    var userIds         : MutableList<String>? = null): Parcelable {

    constructor() : this(null)

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["userIds"]      = userIds

        return hashMap
    }
    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : PrivateChannel {
            val item = PrivateChannel(
                hashMap["userIds"] as MutableList<String>
            )
            return item
        }
    }
}