package edu.indiana.iustudentgovernment.models

import edu.indiana.iustudentgovernment.database
import edu.indiana.iustudentgovernment.models.IusgBranch.EXECUTIVE
import edu.indiana.iustudentgovernment.models.IusgBranch.JUDICIAL
import edu.indiana.iustudentgovernment.models.IusgBranch.LEGISLATIVE
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Statement(
    val id: String,
    val createdAt: Long,
    val createdByUsername: String,
    var branch: IusgBranch,
    var lastEditTime: Long?,
    var lastEditedByUsername: String?,
    var title: String,
    var rawMarkdown: String
) : Idable {
    val author get() = database.getMember(createdByUsername)!!
    val date get() = createdAt.getAsDate()

    val lastEditDate get() = lastEditTime?.getAsDate()
    val lastEditAuthor get() = lastEditedByUsername?.let { database.getMember(it) }
    val text get() = HtmlRenderer.builder().build().render(Parser.builder().build().parse(rawMarkdown))

    val isJudicial = branch == JUDICIAL
    val isLegislative = branch == LEGISLATIVE
    val isExecutive = branch == EXECUTIVE

    override fun getPermanentId() = id
}

private fun Long.getAsDate() = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    .format(DateTimeFormatter.ISO_LOCAL_DATE)

enum class IusgBranch {
    EXECUTIVE,
    LEGISLATIVE,
    JUDICIAL
}