package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class Calendar (
    var date                      : Date? = null,
    var name                      : String? = null,
    var description               : String? = null): Parcelable{

    constructor() : this(null,null,null)

    fun toHashMap() : HashMap<String?, Any?>{
        val hashMap = HashMap<String?, Any?>()
        hashMap["name"]                  = name
        hashMap["description"]           = description
        hashMap["date"]                  = date
        return hashMap
    }

    companion object{
        fun fromHash(hashMap: HashMap<String,Any?>) : Calendar {
            val item = Calendar(
                hashMap["date"] as Date,
                hashMap["name"].toString(),
                hashMap["description"].toString(),

            )
            return item
        }
    }
}