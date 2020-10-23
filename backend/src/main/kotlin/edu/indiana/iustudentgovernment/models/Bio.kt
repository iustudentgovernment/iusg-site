package edu.indiana.iustudentgovernment.models

import edu.indiana.iustudentgovernment.database
import edu.indiana.iustudentgovernment.models.Branches.EXECUTIVE
import edu.indiana.iustudentgovernment.models.Branches.JUDICIAL
import edu.indiana.iustudentgovernment.models.Branches.LEGISLATIVE
import edu.indiana.iustudentgovernment.models.Branches.ELECTION_COMMISSION

data class Bio(
        val id: String,
        val firstName: String,
        val lastName: String,
        val title: String,
        val email: String,
        val jobDescription: String,
        val branch: Branches
) : Idable{
    val isJudicial = branch == JUDICIAL
    val isLegislative = branch == LEGISLATIVE
    val isExecutive = branch == EXECUTIVE
    val isEC = branch == ELECTION_COMMISSION
    val profilePictureLink = "https://iustudentgovernment.indiana.edu/images/$firstName$lastName$title.jpg"
    override fun getPermanentId() = id
}

enum class Branches {
    EXECUTIVE,
    LEGISLATIVE,
    JUDICIAL,
    ELECTION_COMMISSION
}