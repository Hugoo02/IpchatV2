package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class FileModel (
    var name        : String? = null,
    var size        : Double? = null,
    var path        : String? = null,
    var type        : String? = null,
    var extension   : String? = null,
    val subFiles    : Int? = 0) : Parcelable {

    constructor() : this(null, null, null, null, null, 0)

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["name"]             = name
        hashMap["size"]             = size
        hashMap["path"]             = path
        hashMap["type"]             = type
        hashMap["extension"]        = extension
        hashMap["subFiles"]         = subFiles

        return hashMap
    }
    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : FileModel {
            val item = FileModel(
                hashMap["name"].toString(),
                hashMap["size"] as Double,
                hashMap["path"].toString(),
                hashMap["type"].toString(),
                hashMap["extension"].toString(),
                hashMap["subFiles"] as Int
            )
            return item
        }
    }

    }