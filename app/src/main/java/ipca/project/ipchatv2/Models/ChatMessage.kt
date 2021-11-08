package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ChatMessage (

    var id                      : String? = null,
    var text                    : String? = null,
    var fromId                  : String? = null,
    var toId                    : String? = null,
    var timeStamp               : Long? = null): Parcelable {

    constructor() : this("", "", "", "", -1)

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["id"]               = id
        hashMap["text"]             = text
        hashMap["fromId"]           = fromId
        hashMap["toId"]             = toId
        hashMap["timeStamp"]        = timeStamp

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : User {
            val item = User(
                hashMap["id"].toString(),
                hashMap["text"].toString(),
                hashMap["fromId"].toString(),
                hashMap["toId"].toString(),
                hashMap["timeStamp"].toString(),
            )
            return item
        }
    }
}