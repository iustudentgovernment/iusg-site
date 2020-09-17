package edu.indiana.iustudentgovernment.data

import edu.indiana.iustudentgovernment.authentication.Member
import edu.indiana.iustudentgovernment.authentication.Role
import edu.indiana.iustudentgovernment.data.CommitteeRole.MEMBER
import edu.indiana.iustudentgovernment.models.Idable
import edu.indiana.iustudentgovernment.database

data class Committee(
        val formalName: String,
        val id: String,
        val permissionLevelForEntry: Int,
        val committeeType: CommitteeType,
        var committeeMemberships: MutableList<CommitteeMembership> = mutableListOf(),
        var descriptionId: String = "committee_description_$id"
) : Idable {
    override fun getPermanentId() = id

    fun isPrivileged(user: Member) =
            user.username in committeeMemberships.filter { it.committeeRole != MEMBER }.map { it.username } || user.isAdministrator()

    val chairs get() = committeeMemberships.filter { it.committeeRole != MEMBER }.map { it.member }
    val membersString get() = committeeMemberships.joinToString(", ") { it.member.asLink() }
    val upcomingMeetings get() = database.getFutureMeetings().filter { it.committeeId == id }.take(3)
    val pastMeetings get() = database.getPastMeetings().filter { it.committeeId == id }.take(3)
    val activeLegislation get() = database.getActiveLegislation().filter { it.committeeId == id }
    val enactedLegislation get() = database.getEnactedLegislation().filter { it.committeeId == id }
    val failedLegislation get() = database.getFailedLegislation().filter { it.committeeId == id }
    val asLink get() = "<a href='/committees/$id'>$formalName</a>"
    val description get() = database.getMessage(descriptionId)!!.value.toString()
}

data class CommitteeMembership(val username: String, val id: String, val committeeId: String, val committeeRole: CommitteeRole) : Idable {
    val committee get() = database.getCommittee(committeeId)!!
    val member get() = database.getMember(username)!!

    override fun getPermanentId() = id
}

enum class CommitteeType {
    CONGRESS, EXECUTIVE
}

enum class CommitteeRole(val readable: String) {
    MEMBER("Member"), COMMITTEE_CHAIR("Chair")
}