package edu.indiana.iustudentgovernment.controllers

import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.Route

fun Route.staticContentRoutes() {
    static("static") {
        resources("static")
    }
}