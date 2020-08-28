package edu.indiana.iustudentgovernment.controllers

import edu.indiana.iustudentgovernment.authentication.getUser
import edu.indiana.iustudentgovernment.data.getMap
import edu.indiana.iustudentgovernment.http.HandlebarsContent
import edu.indiana.iustudentgovernment.http.respondHbs
import edu.indiana.iustudentgovernment.models.IusgBranch.EXECUTIVE
import edu.indiana.iustudentgovernment.models.IusgBranch.JUDICIAL
import edu.indiana.iustudentgovernment.models.IusgBranch.LEGISLATIVE
import io.ktor.application.call
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.editSiteRoutes() {
    route("/edit") {
        get("") {
            val user = call.getUser() ?: return@get call.respondRedirect("/")

            val map = call.getMap("Edit Site", "edit")
            map["needsRivet"] = true

            if (user.title.isNotEmpty() && user.title.maxBy { it.rank }!!.rank >= 3) {
                if (user.branch == JUDICIAL || user.isAdministrator()) map["judicial"] = true
                if (user.branch == LEGISLATIVE || user.isAdministrator()) map["legislative"] = true
                if (user.branch == EXECUTIVE || user.isAdministrator()) map["executive"] = true
            } else return@get call.respondRedirect("/")



            call.respondHbs(HandlebarsContent("/edit-site/edit-site-home.hbs", map))
        }
    }
}