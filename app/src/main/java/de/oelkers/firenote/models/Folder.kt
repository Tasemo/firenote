package de.oelkers.firenote.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Folder(
    var name: String? = null,
    val notes: MutableList<Note> = ArrayList(),
) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.createTypedArrayList(Note)!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeTypedList(notes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Folder> {
        override fun createFromParcel(parcel: Parcel): Folder {
            return Folder(parcel)
        }

        override fun newArray(size: Int): Array<Folder?> {
            return arrayOfNulls(size)
        }
    }
}
