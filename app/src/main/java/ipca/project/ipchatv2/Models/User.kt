package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    var address             : String? = null,
    var course              : String? = null,
    var email               : String? = null,
    var gender              : String? = null,
    var id                  : String? = null,
    var imageURL            : String? = null,
    var username            : String? = null,
    var token               : String? = null,
    //var year      : Int? = null,
    var student_number      : String? = null): Parcelable{

    constructor() : this(null, null, null, null, null,
        null, null, null,null)

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["address"]          = address
        hashMap["course"]           = course
        hashMap["email"]            = email
        hashMap["gender"]           = gender
        hashMap["id"]               = id
        hashMap["imageURL"]         = imageURL
        hashMap["username"]         = username
        hashMap["token"]            = token
        hashMap["student_number"]   = student_number

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : User {
            val item = User(
                hashMap["address"].toString(),
                hashMap["course"].toString(),
                hashMap["email"].toString(),
                hashMap["gender"].toString(),
                hashMap["id"].toString(),
                hashMap["imageURL"].toString(),
                hashMap["username"].toString(),
                hashMap["token"].toString(),
                hashMap["student_number"].toString()
            )
            return item
        }
    }
}