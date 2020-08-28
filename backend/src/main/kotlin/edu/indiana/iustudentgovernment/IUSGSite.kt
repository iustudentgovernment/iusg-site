package edu.indiana.iustudentgovernment

import edu.indiana.iustudentgovernment.controllers.contactRoutes
import edu.indiana.iustudentgovernment.controllers.homeRoutes
import edu.indiana.iustudentgovernment.controllers.peripherals
import edu.indiana.iustudentgovernment.controllers.staticContentRoutes
import edu.indiana.iustudentgovernment.controllers.studentRightsRoutes
import edu.indiana.iustudentgovernment.http.statusConfiguration
import edu.indiana.iustudentgovernment.authentication.User
import edu.indiana.iustudentgovernment.authentication.casRoutes
import edu.indiana.iustudentgovernment.controllers.statementsRoutes
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.SessionStorageMemory
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(StatusPages) {
        statusConfiguration()
    }
    install(Sessions) {
        cookie<User>("user", storage = SessionStorageMemory())
    }

    install(Routing) {
        staticContentRoutes()

        homeRoutes()
        contactRoutes()
        peripherals()
        studentRightsRoutes()
        statementsRoutes()
        casRoutes()
    }

            if (cleanse) database.insertInitial()
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