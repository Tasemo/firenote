package de.oelkers.firenote.util

import android.os.Parcel
import java.time.LocalDateTime

fun Parcel.writeLocalDateTime(dateTime: LocalDateTime?) = writeString(dateTime.toString())

fun Parcel.readLocalDateTime(): LocalDateTime? {
    val value = readString()
    return if (value.isNullOrEmpty() || value == "null") {
        null
    } else {
        LocalDateTime.parse(value)
    }
}
