package edu.indiana.iustudentgovernment.controllers

import edu.indiana.iustudentgovernment.data.getMap
import edu.indiana.iustudentgovernment.http.HandlebarsContent
import edu.indiana.iustudentgovernment.http.respondHbs
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.studentRightsRoutes() {
    route("/student-rights") {
        get("") {
            val map = call.getMap("Student Rights", "studentrights")

            call.respondHbs(HandlebarsContent("student-rights/index.hbs", map))
        }

        get("/request-help") {
            val map = call.getMap("Get Help", "studentrights")

            call.respondHbs(HandlebarsContent("student-rights/request-help.hbs", map))
        }

        get("/apply-sr") {
            val map = call.getMap("Apply to Student Rights", "studentrights")

            call.respondHbs(HandlebarsContent("student-rights/apply-sr.hbs", map))
        }
    }
}