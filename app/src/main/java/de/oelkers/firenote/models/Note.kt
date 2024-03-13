package de.oelkers.firenote.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalDateTime

fun parseLocalDateTime(value: String?): LocalDateTime? {
    return if (value.isNullOrEmpty() || value == "null") {
        null
    } else {
        LocalDateTime.parse(value)
    }
}

data class Note(
    val id: String,
    var title: String? = null,
    var content: String? = null,
    var created: LocalDateTime? = null,
    var audioPath: String? = null
) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parseLocalDateTime(parcel.readString()),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(created.toString())
        parcel.writeString(audioPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}
