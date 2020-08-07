package edu.indiana.iusg.controllers

import edu.indiana.iusg.data.getMap
import edu.indiana.iusg.http.HandlebarsContent
import edu.indiana.iusg.http.respondHbs
import io.ktor.application.call
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.peripherals() {
    get("/privacy") {
        val map = getMap("Privacy Notice", "privacy")

        call.respondHbs(HandlebarsContent("privacy.hbs", map))
    }
}