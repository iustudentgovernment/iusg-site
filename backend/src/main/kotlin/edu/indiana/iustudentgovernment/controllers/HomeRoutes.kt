package edu.indiana.iustudentgovernment.controllers

import edu.indiana.iustudentgovernment.http.HandlebarsContent
import edu.indiana.iustudentgovernment.data.getMap
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import edu.indiana.iustudentgovernment.http.respondHbs

fun Route.homeRoutes() {
    route("/") {
        get("") {
            val map = call.getMap("Home", "home")

            call.respondHbs(HandlebarsContent("index.hbs", map))
        }
    }
}