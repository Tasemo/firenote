package de.oelkers.firenote.models

import android.os.Parcel
import android.os.Parcelable
import de.oelkers.firenote.util.readLocalDateTime
import de.oelkers.firenote.util.writeLocalDateTime
import java.io.Serializable
import java.time.LocalDateTime

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
        parcel.readLocalDateTime(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeLocalDateTime(created)
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
