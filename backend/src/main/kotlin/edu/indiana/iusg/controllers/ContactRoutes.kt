package edu.indiana.iusg.controllers

import edu.indiana.iusg.data.getMap
import edu.indiana.iusg.http.HandlebarsContent
import edu.indiana.iusg.http.respondHbs
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.contactRoutes() {
    route("/contact") {
        get("") {
            val map = getMap("Contact Us", "contact")
            call.respondHbs(HandlebarsContent("contact-us.hbs", map))
        }
        get("/received") {
            val map = getMap("Contact Request Received", "contact")
            call.respondHbs(HandlebarsContent("/contact/contact-received.hbs", map))
        }
        get("/press") {
            val map = getMap("Contact Us | Press", "contact")
            call.respondHbs(HandlebarsContent("/contact/contact-press.hbs", map))
        }
        post("") {
            val postParameters: Parameters = call.receiveParameters()
            val nullError = "A parameter was not provided"
            val emailConfirmError = "Your emails did not match"

            val name = postParameters["name"] ?: return@post call.respondRedirect("/contact?error=$nullError")
            val email = postParameters["email"] ?: return@post call.respondRedirect("/contact?error=$nullError")
            val confirmEmail = postParameters["confirmemail"]
                    ?: return@post call.respondRedirect("/contact?error=$nullError")
            val subject = postParameters["subject"] ?: return@post call.respondRedirect("/contact?error=$nullError")
            val message = postParameters["message"] ?: return@post call.respondRedirect("/contact?error=$nullError")
            val pressInquiry = postParameters["ispress"]?.toBoolean() ?: false
            val organization = postParameters["organization"]

            if (email != confirmEmail) return@post call.respondRedirect("/contact?error=$emailConfirmError")


            call.respondRedirect("/contact/received")
        }

    }
}