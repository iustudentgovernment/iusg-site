package edu.indiana.iustudentgovernment.models

import edu.indiana.iustudentgovernment.database
import edu.indiana.iustudentgovernment.models.Note
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

data class Meeting(
        val name: String,
        val meetingId: String,
        val time: Long,
        val location: String,
        val committeeId: String,
        var agendaFileUrl: String?,
        val ledBy: List<String>,
        val notes: List<Note>,
        val minutesUrl: String? = null
) : Idable {
    val date
        get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("hh:mm, MM/dd/YYYY"))

    val committee get() = database.getCommittee(committeeId)
    val ledByString get() = ledBy.filter { it.isNotBlank() }.joinToString(", ") { database.getMember(it)!!.asLink }
    val notesString get() = notes.joinToString("\n") { it.text }
    val ledByNoLinkString get() = ledBy.filter { it.isNotBlank() }.joinToString(", ")
    val jsTime get() = SimpleDateFormat("yyyy-MM-dd'T'hh:mm").format(Date.from(Instant.ofEpochMilli(time)))

    override fun getPermanentId() = meetingId

}
