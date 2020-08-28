package edu.indiana.iustudentgovernment.models

import edu.indiana.iustudentgovernment.models.Idable

data class Message(val id: String, val value: Any) : Idable {
    override fun getPermanentId() = id
}