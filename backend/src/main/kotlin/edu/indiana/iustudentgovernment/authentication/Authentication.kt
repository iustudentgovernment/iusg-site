package edu.indiana.iustudentgovernment.authentication

import edu.indiana.iustudentgovernment.models.Idable
import edu.indiana.iustudentgovernment.database
import edu.indiana.iustudentgovernment.models.IusgBranch
import io.ktor.application.ApplicationCall
import io.ktor.sessions.get
import io.ktor.sessions.sessions

enum class Role(val readable: String) {
    MEMBER("Member"), PRIVILEGED_MEMBER("Member"), COMMITTEE_CHAIR("Chair")
}

enum class Title(val readable: String, val rank: Int, val note: String? = null) {
    MEMBER("Member", 0),

    // congress
    CONGRESSPERSON("Representative", 1),
    COMMITTEE_CHAIR("Committee Chair", 1),
    PARLIAMENTARIAN("Parliamentarian", 2),
    GRAMMARIAN("Grammarian", 2),
    PRESS_SECRETARY("Press Secretary", 2),
    SPEAKER("Speaker", 3),

    // exec
    SITE_ADMINISTRATOR("Site Administrator", 4),
    POLICY_DIRECTOR("Policy Director", 0),
    TREASURER("Treasurer", 2),
    CTO("CTO", 4),
    PRESIDENT("President", 4),
    VICE_PRESIDENT("Vice President", 4),
    CHIEF_OF_STAFF("Chief of Staff", 4),
    DEPUTY_CHIEF_OF_STAFF("Deputy Chief of Staff", 4),
    CONGRESSIONAL_SECRETARY("Congressional Secretary", 4),


    // supreme court
    SUPREME_COURT_JUSTICE("Supreme Court Associate Justice", 0),
    SUPREME_COURT_CHIEF_JUSTICE("Chief Justice", 2),

    // silc
    ADVISOR("Advisor", 4)
}

data class Member(
    val username: String,
    val constituency: String?,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val title: List<Title>,
    var branch: IusgBranch,
    val bio: String? = null,
    var active: Boolean = true
) : Idable {
    val titles get() = title.sortedBy { it.rank }.joinToString(", ") { it.readable }
    val committeeMemberships get() = database.getCommitteeMembershipsForMember(username)

    val asLink get() = asLink()

    fun isAdministrator() = title.map { it.rank }.max()!! >= 3
    fun asLink() = "<a href='/members/$username'>$name</a>"

    val steering get() = title.map { it.rank }.max()!! >= 2

    override fun getPermanentId() = username
}

fun ApplicationCall.getUser() = sessions.get<User>()?.let { database.getMember(it.userId) }

fun List<Title>.hasTitlePermission(title: Title): Boolean = maxBy { it.rank }!!.rank >= title.rank

fun List<String>.toMembersLink() = map { database.getMember(it)!! }.allAsLink()
fun List<Member>.allAsLink() = joinToString(", ") { it.asLink }

class User(val userId: String)