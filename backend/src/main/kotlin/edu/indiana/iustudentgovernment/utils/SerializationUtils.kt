package edu.indiana.iustudentgovernment.utils

import com.google.gson.Gson
import com.rethinkdb.RethinkDB.r
import com.rethinkdb.net.Cursor
import edu.indiana.iustudentgovernment.connection
import edu.indiana.iustudentgovernment.gson
import org.json.simple.JSONObject


fun <T> asPojo(gson: Gson, map: HashMap<*, *>?, tClass: Class<T>): T? {
    return gson.fromJson(JSONObject.toJSONString(map), tClass)
}

/**
 * Iterate through a rethinkdb [Cursor], mapping each value into type [T] using Gson
 */
fun <T> Any.queryAsArrayList(gson: Gson, t: Class<T>): MutableList<T?> {
    val tS = mutableListOf<T?>()
    @Suppress("UNCHECKED_CAST") val cursor = this as Cursor<HashMap<*, *>>
    cursor.forEach { hashMap -> tS.add(asPojo(gson, hashMap, t)) }
    cursor.close()
    return tS
}

fun <T> T.insert(table: String) = r.table(table).insert(r.json(gson.toJson(this))).run<Any>(connection)
fun <T> T.update(table: String, id: String) =
    r.table(table).get(id).update(r.json(gson.toJson(this))).run<Any>(connection)

fun delete(table: String, id: String) = r.table(table).get(id).delete().run<Any>(connection)
inline fun <reified T> getObject(table: String, id: String) =
    asPojo(gson, r.table(table).get(id).run(connection), T::class.java)

inline fun <reified T> getAll(table: String) = r.table(table).run<Any>(connection).queryAsArrayList(gson, T::class.java)