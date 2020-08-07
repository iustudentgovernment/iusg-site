package edu.indiana.iusg

import edu.indiana.iusg.controllers.contactRoutes
import edu.indiana.iusg.controllers.homeRoutes
import edu.indiana.iusg.controllers.peripherals
import edu.indiana.iusg.controllers.staticContentRoutes
import edu.indiana.iusg.http.statusConfiguration
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(StatusPages) {
        statusConfiguration()
    }

    install(Routing) {
        staticContentRoutes()

        homeRoutes()
        contactRoutes()
        peripherals()
    }
}

fun main() {
    runBlocking {
        launch {
            embeddedServer(
                    Netty,
                    System.getenv("PORT")?.toInt() ?: 80,
                    watchPaths =
                    if (System.getenv("PORT")?.toInt() == null) listOf("backend")
                    else listOf(),
                    module = Application::module)
                    .start()
        }
    }
}