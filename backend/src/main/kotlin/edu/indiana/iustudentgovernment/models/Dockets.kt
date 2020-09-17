package edu.indiana.iustudentgovernment.models

data class Complaint(
        val id: String,
        val name: String,
        val documentUrls: List<String>
): Idable {
    override fun getPermanentId() = id
}

data class SupremeCourtDecision(
        val id: String,
        val name: String,
        val documentUrls: List<String>
): Idable {
    override fun getPermanentId() = id
}