package ipca.project.ipchatv2.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class ChatMessage (

    var id                      : String,
    var text                    : String,
    var fromId                  : String,
    var toId                    : String,
    var seen                    : Boolean,
    var timeStamp               : Long) {

    constructor() : this("", "", "", "", false, -1)


}