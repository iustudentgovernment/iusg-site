package edu.indiana.iustudentgovernment.controllers

import edu.indiana.iustudentgovernment.data.getMap
import edu.indiana.iustudentgovernment.http.HandlebarsContent
import edu.indiana.iustudentgovernment.http.respondHbs
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.aboutRoutes() {
    route("/about") {
        get("") {
            val map = call.getMap("About", "about")

            call.respondHbs(HandlebarsContent("/about/about-home.hbs", map))
        }

        get("/budget") {
            val map = call.getMap("Budget", "about")

            call.respondHbs(HandlebarsContent("/about/budget.hbs", map))
        }

        get("/recent-updates") {
            val map = call.getMap("Accomplishments and Updates", "about")

            call.respondHbs(HandlebarsContent("/about/accomplishments.hbs", map))
        }
    }
}