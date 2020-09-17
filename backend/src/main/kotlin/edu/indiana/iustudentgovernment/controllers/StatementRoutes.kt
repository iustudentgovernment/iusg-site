package edu.indiana.iustudentgovernment.controllers

import edu.indiana.iustudentgovernment.authentication.Member
import edu.indiana.iustudentgovernment.authentication.getUser
import edu.indiana.iustudentgovernment.data.getMap
import edu.indiana.iustudentgovernment.database
import edu.indiana.iustudentgovernment.http.HandlebarsContent
import edu.indiana.iustudentgovernment.http.respondHbs
import edu.indiana.iustudentgovernment.models.IusgBranch
import edu.indiana.iustudentgovernment.models.IusgBranch.EXECUTIVE
import edu.indiana.iustudentgovernment.models.IusgBranch.JUDICIAL
import edu.indiana.iustudentgovernment.models.IusgBranch.LEGISLATIVE
import edu.indiana.iustudentgovernment.models.Statement
import edu.indiana.iustudentgovernment.utils.nullifyEmpty
import io.ktor.application.call
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

private fun canEditStatements(member: Member) = member.title.map { it.rank }.max()!! >= 2

fun Route.statementsRoutes() {
    route("/statements") {
        get("") {
            val map = call.getMap("Statements", "statements")

            val type = call.request.queryParameters["type"]

            val statements = database.getStatements()
            if (type == null || type == "executive") map["exec-statements"] = statements.filter { it.branch == EXECUTIVE }
            if (type == null || type == "congress") map["congress-statements"] = statements.filter { it.branch == LEGISLATIVE }
            if (type == null || type == "sc") map["sc-statements"] = statements.filter { it.branch == JUDICIAL }

            map["statementsSize"] = database.getStatements().size
            map["isPrivileged"] = call.getUser()?.let { canEditStatements(it) }

            call.respondHbs(HandlebarsContent("/statements/statements-home.hbs", map))
        }

        get("/{id}") {
            val statement = database.getStatement(call.parameters["id"]!!)
            if (statement == null) call.respondRedirect("/statements")
            else {
                val map = call.getMap("Statements | ${statement.title}", "statements")
                map["statement"] = statement
                map["isPrivileged"] = call.getUser()?.let { canEditStatements(it) }

                call.respondHbs(HandlebarsContent("/statements/statement.hbs", map))
            }
        }
    }

    get("/create-statement") {
        val user = call.getUser()
        if (user == null || !canEditStatements(user)) call.respondRedirect("/statements")
        else {
            val map = call.getMap("New Statement", "statements")

            call.respondHbs(HandlebarsContent("/statements/new-statement.hbs", map))
        }
    }

    get("/edit-statement/{id}") {
        val user = call.getUser()
        val statement = database.getStatement(call.parameters["id"]!!)
        if (statement == null || user == null || !canEditStatements(user)) call.respondRedirect("/statements")
        else {
            val map = call.getMap("Edit Statement", "statements")
            map["statement"] = statement
            map["isPrivileged"] = call.getUser()?.let { canEditStatements(it) }

            call.respondHbs(HandlebarsContent("/statements/edit-statement.hbs", map))
        }

    }

    get("/delete-statement/{id}") {
        val user = call.getUser()
        val statement = database.getStatement(call.parameters["id"]!!)
        if (user == null || statement == null || !canEditStatements(user)) call.respondRedirect("/statements")
        else {
            database.deleteStatement(statement.id)

            call.respondRedirect("/statements")
        }
    }

    get("/create-new-statement") {
        val user = call.getUser()
        if (user == null || !canEditStatements(user)) call.respondRedirect("/statements")
        else {
            val time = System.currentTimeMillis()
            val title = call.request.queryParameters["title"]!!
            val text = call.request.queryParameters["text"]!!
            val id = call.request.queryParameters["statementId"]?.nullifyEmpty()
            val branch = IusgBranch.valueOf(call.request.queryParameters["branch"]!!)
            println(branch)
            val statement = if (id == null) {
                val tmp = Statement(
                        id ?: database.getUuid(),
                        time,
                        user.username,
                        branch,
                        null,
                        null,
                        title,
                        text
                )

                database.insertStatement(tmp)
                tmp
            } else {
                val tmp = database.getStatement(id)!!.copy(
                        lastEditTime = System.currentTimeMillis(),
                        lastEditedByUsername = user.username,
                        branch = branch,
                        rawMarkdown = text,
                        title = title
                )
                database.updateStatement(tmp)
                tmp
            }

            call.respondRedirect("/statements/${statement.id}")
        }
    }
}