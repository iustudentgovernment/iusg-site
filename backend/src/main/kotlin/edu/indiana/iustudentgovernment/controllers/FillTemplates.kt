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
import edu.indiana.iustudentgovernment.models.Bio
import edu.indiana.iustudentgovernment.utils.nullifyEmpty
import io.ktor.application.call
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

