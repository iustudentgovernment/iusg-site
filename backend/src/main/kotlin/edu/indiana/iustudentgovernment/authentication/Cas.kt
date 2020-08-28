package edu.indiana.iustudentgovernment.authentication

import edu.indiana.iustudentgovernment.callbackUrl
import edu.indiana.iustudentgovernment.casUrl
import edu.indiana.iustudentgovernment.database
import io.ktor.application.call
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.sessions.clear
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.jsoup.Jsoup

fun Route.casRoutes() {
    route("/cas") {
        get("/callback") {
            val casTicket = call.request.queryParameters["casticket"]
            if (casTicket != null) {
                val text =
                    Jsoup.connect("https://cas.iu.edu/cas/validate?cassvc=IU&casticket=$casTicket&casurl=$callbackUrl")
                        .get().body().text()

                if (text == "no") call.respondRedirect("/callback")
                else {
                    val user = database.getMember(text.split(" ")[1])
                    if (user == null || !user.active) call.respondRedirect("/")
                    else {
                        call.sessions.set(User(user.username))
                        call.respondRedirect("/?login=true")
                    }
                }
            } else call.respondRedirect(casUrl)
        }
    }

    get("/login") {
        call.respondRedirect(casUrl)
    }

    get("/logout") {
        call.sessions.clear<User>()
        call.respondRedirect("https://cas.iu.edu/cas/logout")
    }

}