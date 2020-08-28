package edu.indiana.iustudentgovernment

import com.google.gson.Gson
import com.rethinkdb.RethinkDB
import com.rethinkdb.RethinkDB.*
import com.rethinkdb.net.Connection
import edu.indiana.iustudentgovernment.data.Database

val fromEmail = "aratzman@iu.edu"
val emailTest = true

val gson = Gson()

private val databaseHostname = System.getenv("database_hostname")
internal val urlBase = System.getenv("url_base")
val cleanse = System.getenv("cleanse")!!.toBoolean()

val callbackUrl: String = "$urlBase/cas/callback"
val casUrl: String = "https://cas.iu.edu/cas/login?cassvc=IU&casurl=$callbackUrl"

val connection: Connection =
    if (databaseHostname == "localhost") r.connection().db("iusg").hostname(databaseHostname).connect()
    else r.connection().db("iusg").user("admin", System.getenv("dbpassword")).hostname(databaseHostname).connect()

val database: Database = (if (databaseHostname == "localhost") {
    Database(cleanse)
} else Database(false))

val savedPages = mutableMapOf<String, String>()

