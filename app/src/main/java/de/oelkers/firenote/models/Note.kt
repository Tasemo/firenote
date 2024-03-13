package de.oelkers.firenote.models

import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

data class Note(
    var title: String?,
    var content: String?,
    var created: LocalDateTime?,
    var audioPath: String?
) : Serializable
