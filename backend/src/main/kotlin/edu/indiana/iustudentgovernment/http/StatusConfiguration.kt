package edu.indiana.iustudentgovernment.http

import edu.indiana.iustudentgovernment.data.getMap
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText

fun StatusPages.Configuration.statusConfiguration() {
    exception<Throwable> { throwable ->
        throwable.printStackTrace()
        call.respondText(throwable.message
                ?: throwable.localizedMessage, ContentType.Any, HttpStatusCode.InternalServerError)
    }
    status(HttpStatusCode.NotFound) {
        call.respondHbs(HandlebarsContent("404.hbs", call.getMap("Not Found", "404")))
    }
}