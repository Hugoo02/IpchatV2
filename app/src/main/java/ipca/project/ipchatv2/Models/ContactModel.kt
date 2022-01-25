package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class ContactModel (
    var name                 : String? = null,
    var schoolName           : String? = null,
    var address              : String? = null,
    var city                 : String? = null,
    var phoneNumber          : String? = null,
    var email                : String? = null): Parcelable{

    constructor() : this(null,null,null)

    fun toHashMap() : HashMap<String?, Any?>{
        val hashMap = HashMap<String?, Any?>()
        hashMap["name"]                 = name
        hashMap["schoolName"]           = schoolName
        hashMap["address"]              = address
        hashMap["city"]                 = city
        hashMap["phoneNumber"]          = phoneNumber
        hashMap["email"]                = email
        return hashMap
    }

    companion object{
        fun fromHash(hashMap: HashMap<String,Any?>) : ContactModel {
            val item = ContactModel(
                hashMap["name"].toString(),
                hashMap["schoolName"].toString(),
                hashMap["address"].toString(),
                hashMap["city"].toString(),
                hashMap["phoneNumber"].toString(),
                hashMap["email"].toString()

            )
            return item
        }
    }
}