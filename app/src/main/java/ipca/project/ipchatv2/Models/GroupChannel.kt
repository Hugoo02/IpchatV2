package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class GroupChannel (
    var chatName    : String? = null,
    var groupImageURL        : String? = null): Parcelable {

    constructor() : this(null, null)

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["chatName"]                  = chatName
        hashMap["groupImageURL"]             = groupImageURL

        return hashMap
    }
    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : GroupChannel {
            val item = GroupChannel(
                hashMap["chatName"].toString(),
                hashMap["groupImageURL"].toString()
            )
            return item
        }
    }
}