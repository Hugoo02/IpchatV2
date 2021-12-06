package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    var id                  : String? = null,
    var username            : String? = null,
    var course              : String? = null,
    var email               : String? = null,
    var address             : String? = null,
    var imageURL            : String? = null,
    var student_number      : String? = null,
    var year                : String? = null,
    var gender              : String? = null)
: Parcelable {

    constructor() : this("", "", "", "", "", "","","","")

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["id"]               = id
        hashMap["username"]         = username
        hashMap["course"]           = course
        hashMap["email"]            = email
        hashMap["address"]          = address
        hashMap["imageURL"]         = imageURL
        hashMap["student_number"]   = student_number
        hashMap["year"]             = year
        hashMap["gender"]           = gender

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : User {
            val item = User(
                hashMap["id"].toString(),
                hashMap["username"].toString(),
                hashMap["course"].toString(),
                hashMap["email"].toString(),
                hashMap["address"].toString(),
                hashMap["imageURL"].toString(),
                hashMap["student_number"].toString(),
                hashMap["year"].toString(),
                hashMap["gender"].toString()
            )
            return item
        }
    }
}