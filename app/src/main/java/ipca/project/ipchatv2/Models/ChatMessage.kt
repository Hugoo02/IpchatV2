package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class ChatMessage (
    var senderId    : String? = null,
    var text        : String? = null,
    var time        : Date? = null,
    var type        : String? = null,
    var messageId   : String? = null): Parcelable {

    constructor() : this(null, null, null, null, null)

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["senderId"]         = senderId
        hashMap["text"]             = text
        hashMap["time"]             = time
        hashMap["type"]             = type
        hashMap["messageId"]        = messageId

        return hashMap
    }
    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : ChatMessage {
            val item = ChatMessage(
                hashMap["senderId"].toString(),
                hashMap["text"].toString(),
                hashMap["time"] as Date,
                hashMap["type"].toString(),
                hashMap["messageId"].toString()
            )
            return item
        }
    }
}