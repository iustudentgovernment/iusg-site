package edu.indiana.iustudentgovernment.controllers

import edu.indiana.iustudentgovernment.data.getMap
import edu.indiana.iustudentgovernment.http.HandlebarsContent
import edu.indiana.iustudentgovernment.http.respondHbs
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.peripherals() {
    get("/privacy") {
        val map = call.getMap("Privacy Notice", "privacy")

        call.respondHbs(HandlebarsContent("privacy.hbs", map))
    }
}