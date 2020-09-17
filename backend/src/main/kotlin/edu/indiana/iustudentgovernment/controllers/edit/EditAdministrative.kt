package edu.indiana.iustudentgovernment.controllers.edit

import edu.indiana.iustudentgovernment.authentication.Member
import edu.indiana.iustudentgovernment.authentication.Title
import edu.indiana.iustudentgovernment.authentication.getUser
import edu.indiana.iustudentgovernment.data.CommitteeRole.COMMITTEE_CHAIR
import edu.indiana.iustudentgovernment.data.getMap
import edu.indiana.iustudentgovernment.database
import edu.indiana.iustudentgovernment.http.HandlebarsContent
import edu.indiana.iustudentgovernment.http.respondHbs
import edu.indiana.iustudentgovernment.models.IusgBranch
import edu.indiana.iustudentgovernment.models.IusgBranch.EXECUTIVE
import edu.indiana.iustudentgovernment.utils.nullifyEmpty
import io.ktor.application.call
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.editAdministrative() {
    route("/edit/administrative") {
        get("") {
            val user = call.getUser() ?: return@get call.respondRedirect("/")
            if (!user.isAdministrator()) return@get call.respondRedirect("/edit")

            val map = call.getMap("Edit Site", "edit")
            map["members"] = database.getMembers() + Member("", null, "", "", null, listOf(), EXECUTIVE)
            map["titles"] = Title.values()
            map["branches"] = IusgBranch.values()

            call.respondHbs(HandlebarsContent("edit-site/edit-administrative.hbs", map))
        }

        post("/{type}") {
            try {
                val user = call.getUser() ?: return@post call.respondRedirect("/")
                if (!user.isAdministrator()) return@post call.respondRedirect("/edit")

                val type = call.parameters["type"]!!
                val parameters = call.receiveParameters()

                when (type) {
                    "member" -> {
                        val username = parameters["username"]!!
                        val name = parameters["name"]!!
                        val constituency = parameters["constituency"]!!.nullifyEmpty()
                        val email = parameters["email"]!!
                        val phoneNumber = parameters["phoneNumber"]!!.nullifyEmpty()
                        val branch = IusgBranch.valueOf(parameters["branch"]!!)
                        val titles = parameters["titles"]!!.split(",").map { Title.valueOf(it) }
                        val bio = parameters["bio"]!!.nullifyEmpty()
                        val active = parameters["active"]!!.toBoolean()

                        val member = Member(username, constituency, name, email, phoneNumber, titles, branch, bio, active)
                        database.updateMember(member)

                        val committeeChairmanships = parameters["committeeChairString"]!!
                                .split(",")
                                .map { database.getCommittee(it)!!.committeeMemberships.first { it.username == username } }
                        val existingChairmanships = database.getMember(username)?.committeeMemberships?.filter { it.committeeRole == COMMITTEE_CHAIR } ?: listOf()

                        existingChairmanships.filter { it !in committeeChairmanships }.forEach { database.deleteCommitteeMembership(it) }
                        committeeChairmanships.filter { it !in existingChairmanships }.forEach { database.insertCommitteeMembership(it) }
                    }
                }

                call.respondRedirect("/edit/administrative")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class Test(val email: String)