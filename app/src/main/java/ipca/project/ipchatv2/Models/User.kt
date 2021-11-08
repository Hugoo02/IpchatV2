package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    var username            : String? = null,
    var course              : String? = null,
    var email               : String? = null,
    var address             : String? = null,
    var imageURL            : String? = null,
    var student_number      : String? = null): Parcelable{
    //var year                : Int? = null): Parcelable {

    constructor() : this("", "", "", "", "", "")

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["username"]         = username
        hashMap["course"]           = course
        hashMap["email"]            = email
        hashMap["address"]          = address
        hashMap["imageURL"]         = imageURL
        hashMap["student_number"]   = student_number
        //hashMap["year"]            = year

        return hashMap
    }

    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : User {
            val item = User(
                hashMap["username"].toString(),
                hashMap["course"].toString(),
                hashMap["email"].toString(),
                hashMap["address"].toString(),
                hashMap["imageURL"].toString(),
                hashMap["student_number"].toString(),
                //hashMap["year"] as Int
            )
            return item
        }
    }
}