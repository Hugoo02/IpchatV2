package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class LastMessageGroup ( val groupId: String?,
                         val messageId: String?,
                         val time: Date?): Parcelable {

    constructor() : this(null, null, null)

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["groupId"]              = groupId
        hashMap["messageId"]            = messageId
        hashMap["time"]                 = time

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : LastMessageGroup {
            val item = LastMessageGroup(
                hashMap["groupId"].toString(),
                hashMap["messageId"].toString(),
                hashMap["time"] as Date
            )
            return item
        }
    }
}