package edu.indiana.iusg.controllers

import edu.indiana.iusg.http.HandlebarsContent
import edu.indiana.iusg.data.getMap
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import edu.indiana.iusg.http.respondHbs

fun Route.homeRoutes() {
    route("/") {
        get("") {
            val map = getMap("Home", "home")

            call.respondHbs(HandlebarsContent("index.hbs", map))
        }
    }
}