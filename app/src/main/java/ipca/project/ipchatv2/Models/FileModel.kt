package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.HashMap

@Parcelize
class FileModel (
    var name        : String? = null,
    var extension   : String? = null) : Parcelable {

    constructor() : this(null, null)

    fun toHashMap() : HashMap<String, Any?>{
        val hashMap = HashMap<String, Any?>()
        hashMap["name"]             = name
        hashMap["extension"]        = extension

        return hashMap
    }
    companion object{
        fun fromHash(hashMap:  HashMap<String, Any?>) : FileModel {
            val item = FileModel(
                hashMap["name"].toString(),
                hashMap["extension"].toString()
            )
            return item
        }
    }

    }