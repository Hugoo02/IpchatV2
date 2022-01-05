package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class CalendarModel (
    var date                      : Date? = null,
    var title                     : String? = null,
    var createdBy                 : String? = null,
    var description               : String? = null,
    var local                     : String? = null): Parcelable{

    constructor() : this(null,null,null)

    fun toHashMap() : HashMap<String?, Any?>{
        val hashMap = HashMap<String?, Any?>()
        hashMap["title"]                    = title
        hashMap["description"]              = description
        hashMap["createdBy"]                = createdBy
        hashMap["date"]                     = date
        hashMap["local"]                    = local
        return hashMap
    }

    companion object{
        fun fromHash(hashMap: HashMap<String,Any?>) : CalendarModel {
            val item = CalendarModel(
                hashMap["date"] as Date,
                hashMap["title"].toString(),
                hashMap["createdBy"].toString(),
                hashMap["description"].toString(),
                hashMap["local"].toString()

            )
            return item
        }
    }
}