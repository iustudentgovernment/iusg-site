package edu.indiana.iustudentgovernment.data

import edu.indiana.iustudentgovernment.authentication.Member
import edu.indiana.iustudentgovernment.authentication.User
import edu.indiana.iustudentgovernment.authentication.getUser
import io.ktor.application.ApplicationCall
import io.ktor.sessions.get
import io.ktor.sessions.sessions

internal fun canEditStatements(member: Member) = member.title.map { it.rank }.max()!! >= 2

fun ApplicationCall.getMap(pageTitle: String, pageId: String, pageDescription: String? = null): MutableMap<String, Any?> {
    val map = mutableMapOf<String, Any?>()

    // head
    map["siteTitle"] = "IU Student Government"
    map["siteDescription"] = pageDescription ?: "Indiana University Student Government Main Site"
    map["pageDescription"] = pageDescription

    // nav
    map["isPrivileged"] = getUser()?.let { canEditStatements(it) }
    map["loggedIn"] = sessions.get<User>() != null

    // page
    map["pageTitle"] = "${map["siteTitle"]}: $pageTitle"
    map["pageId"] = pageId
    return map
}