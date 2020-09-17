package edu.indiana.iustudentgovernment.data

import com.rethinkdb.RethinkDB.r
import edu.indiana.iustudentgovernment.authentication.Member
import edu.indiana.iustudentgovernment.authentication.Title
import edu.indiana.iustudentgovernment.connection
import edu.indiana.iustudentgovernment.data.CommitteeRole.COMMITTEE_CHAIR
import edu.indiana.iustudentgovernment.data.CommitteeType.EXECUTIVE
import edu.indiana.iustudentgovernment.fromEmail
import edu.indiana.iustudentgovernment.gson
import edu.indiana.iustudentgovernment.http.HandlebarsContent
import edu.indiana.iustudentgovernment.http.renderHbs
import edu.indiana.iustudentgovernment.models.Complaint
import edu.indiana.iustudentgovernment.models.Idable
import edu.indiana.iustudentgovernment.models.IndividualVote
import edu.indiana.iustudentgovernment.models.IusgBranch
import edu.indiana.iustudentgovernment.models.Legislation
import edu.indiana.iustudentgovernment.models.Meeting
import edu.indiana.iustudentgovernment.models.Message
import edu.indiana.iustudentgovernment.models.Note
import edu.indiana.iustudentgovernment.models.Statement
import edu.indiana.iustudentgovernment.models.SupremeCourtDecision
import edu.indiana.iustudentgovernment.models.Vote
import edu.indiana.iustudentgovernment.urlBase
import edu.indiana.iustudentgovernment.utils.asPojo
import edu.indiana.iustudentgovernment.utils.createEmail
import edu.indiana.iustudentgovernment.utils.queryAsArrayList
import edu.indiana.iustudentgovernment.utils.sendMessage
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val membersTable = "users"
private val committeesTable = "committees"
private val meetingsTable = "meetings"
private val meetingMinutesTable = "meeting_minutes"
private val legislationTable = "legislation"
private val attendanceTable = "attendance"
private val committeeFilesTable = "committee_files"
private val meetingFilesTable = "meeting_files"
private val votesTable = "votes"
private val statementsTable = "statements"
private val keysTable = "keys"
private val messagesTable = "messages"
private val whitcombTable = "whitcomb"
private val complaintsTable = "complaints"
private val supremeCourtDecisions = "sc_decisions"

private val tables = listOf(
        membersTable to "username",
        legislationTable to "id",
        committeesTable to "id",
        meetingMinutesTable to "fileId",
        meetingsTable to "meetingId",
        attendanceTable to "attendenceId",
        meetingFilesTable to "fileId",
        committeeFilesTable to "fileId",
        votesTable to "voteId",
        statementsTable to "id",
        keysTable to "id",
        messagesTable to "id",
        whitcombTable to "week",
        complaintsTable to "id",
        supremeCourtDecisions to "id"
).toMap()

class Database(val cleanse: Boolean) {

    init {
        println("Starting db setup")

        if (cleanse) {
            if (r.dbList().run<List<String>>(connection).contains("iusg")) {
                r.dbDrop("iusg").run<Any>(connection)
            }
            println("Dropped db")
        }

        println("Inserted db. Inserting tables")
        if (!r.dbList().run<List<String>>(connection).contains("iusg")) {
            r.dbCreate("iusg").run<Any>(connection)
            tables.forEach { (table, key) ->
                if (!r.tableList().run<List<String>>(connection).contains(table)) {
                    if (key != "id") r.tableCreate(table).optArg("primary_key", key).run<Any>(connection)
                    else r.tableCreate(table).run<Any>(connection)
                }
            }

        }

    }

    fun insertInitial() {
        println("inserting")

        // add exec committees
        insertCommittee(
                Committee(
                        "Executive Cabinet",
                        "cabinet",
                        1,
                        EXECUTIVE,
                        mutableListOf(
                                CommitteeMembership(
                                        "raryani",
                                        UUID.randomUUID().toString(),
                                        "cabinet",
                                        COMMITTEE_CHAIR
                                )
                        )
                )
        )

        getCommittees().forEach { committee ->
            insertMessage(
                    Message(
                            committee.descriptionId,
                            "Lorem ipsum dolor sit amet, nisl causae ei vim, an augue persius mel, nam dicit epicurei lucilius in. Ex per solet percipitur, soleat interpretaris ius ei."
                    )
            )
        }

        // add exec members
        insertMember(
                Member(
                        "ajirelan",
                        null,
                        "Andrew Ireland",
                        "ajirelan@iu.edu",
                        "812-205-1226",
                        listOf(Title.CHIEF_OF_STAFF),
                        IusgBranch.EXECUTIVE,
                        "Andrew Ireland is a third-year JD/MBA candidate at the Maurer School of Law. He recently returned from a year abroad studying for his MBA in Seoul, South Korea at Sungkyunkwan University’s Global School of Business. A former Cox Scholar, he graduated magna cum laude in 2017 from the O’Neill School of Public and Environmental Affairs and the Media School with dual degrees in Public Financial Management and Journalism. As Chairman of the IUSG Oversight and Reform Committee (“IORC”), he is especially interested in leveraging technology to increase the effectiveness and transparency of IUSG. In his free time, he enjoys walking his dog Peanut, IU Basketball and Dragon Express."
                )
        )

        insertMember(
                Member(
                        "raranyi",
                        null,
                        "Rachel Aranyi",
                        "raranyi@iu.edu",
                        "867-910-9630",
                        listOf(Title.PRESIDENT),
                        IusgBranch.EXECUTIVE
                )
        )

        insertMember(
                Member(
                        "aratzman",
                        null,
                        "Adam Ratzman",
                        "aratzman@iu.edu",
                        "317-979-8260",
                        listOf(Title.CTO, Title.SITE_ADMINISTRATOR),
                        IusgBranch.EXECUTIVE
                )
        )

        insertMessage(
                Message(
                        "speaker_message",
                        "Lorem ipsum dolor sit amet, nisl causae ei vim, an augue persius mel, nam dicit epicurei lucilius in. Ex per solet percipitur, soleat interpretaris ius ei. An eum clita putant habemus, reque oportere forensibus nam ad, ea mundi consequat argumentum usu. Ut nec atqui ancillae, in sit solum labores detraxit."
                )
        )

        insertMessage(
                Message(
                        "whitcomb_description",
                        "Lorem ipsum dolor sit amet, nisl causae ei vim, an augue persius mel, nam dicit epicurei lucilius in. Ex per solet percipitur, soleat interpretaris ius ei. An eum clita putant habemus, reque oportere forensibus nam ad, ea mundi consequat argumentum usu. Ut nec atqui ancillae, in sit solum labores detraxit."
                )
        )

        // create meeting
        insertMeeting(
                Meeting(
                        "General Body Meeting",
                        getUuid(),
                        1580171400000,
                        "IMU State Room West",
                        "congress",
                        null,
                        listOf("ajirelan"),
                        listOf(
                                Note("aratzman", "This is the first Congressional meeting of spring semester!")
                        )
                )
        )


        // insert example future meeting
        insertMeeting(
                Meeting(
                        "General Body Meeting",
                        getUuid(),
                        1580776200000,
                        "IMU State Room West",
                        "congress",
                        null,
                        listOf("ajirelan"),
                        listOf(
                                Note("aratzman", "Snacks and light refreshments will be provided")
                        )
                )
        )

        // insert example statement
        insertStatement(
                Statement(
                        getUuid(),
                        1571025600000,
                        "ajirelan",
                        IusgBranch.EXECUTIVE,
                        null,
                        null,
                        "test statement",
                        """
                            Paragraph 1.
                            
                            This is paragraph 2.
                            
                            This is paragraph 3.
                            
                            Done
                        """.trimIndent()
                )
        )


    }

    // members
    fun getMember(username: String): Member? = get(membersTable, username)
    fun getMembers() = getAll<Member>(membersTable).filter { it.active }.sortedBy { it.name.split(" ").last() }
    fun insertMember(member: Member) {
        insert(membersTable, member)
        if (member.active && member.branch == IusgBranch.LEGISLATIVE) insertCommitteeMembership(
                CommitteeMembership(
                        member.username,
                        getUuid(),
                        "congress",
                        CommitteeRole.MEMBER
                )
        )
    }

    fun updateMember(member: Member) = update(membersTable, member.username, member)

    fun deleteMember(memberId: String) {
        val member = getMember(memberId)!!
        member.active = false

        updateMember(member)
    }


    // committee memberships
    fun getCommitteeMembershipsFor(username: String) =
            getCommittees()
                    .map { it.committeeMemberships }
                    .flatten()
                    .filter { it.username == username }

    fun getAllCommitteeMemberships() =
            getCommittees()
                    .map { it.committeeMemberships }
                    .flatten()

    fun getCommitteeMembersForCommittee(committee: String) =
            getAllCommitteeMemberships().filter { it.committeeId == committee }

    fun getCommitteeMembershipsForMember(member: String) =
            getAllCommitteeMemberships().filter { it.username == member }

    fun insertCommitteeMembership(committeeMembership: CommitteeMembership) =
            updateCommittee(committeeMembership.apply { println("here") }.committee.apply { println("here2") }.copy(committeeMemberships = committeeMembership.committee.committeeMemberships.apply {
                add(
                        committeeMembership
                )
            }))

    fun updateCommitteeMembership(committeeMembership: CommitteeMembership) =
            updateCommittee(
                    committeeMembership.committee.copy(
                            committeeMemberships = committeeMembership.committee.committeeMemberships.map { if (it.username == committeeMembership.username) committeeMembership else it }
                                    .toMutableList()
                    )
            )

    fun deleteCommitteeMembership(committeeMembership: CommitteeMembership) =
            updateCommittee(committeeMembership.committee.copy(committeeMemberships = committeeMembership.committee.committeeMemberships
                    .filter { it.username != committeeMembership.username }
                    .toMutableList())
            )

    // committees
    fun getCommittee(committeeId: String): Committee? = get(committeesTable, committeeId)
    fun getCommittees() = getAll<Committee>(committeesTable).sortedBy { it.formalName }

    fun insertCommittee(committee: Committee) = insert(committeesTable, committee)

    fun updateCommittee(committee: Committee) = update(committeesTable, committee.id, committee)

    fun deleteCommittee(committeeId: String) = delete(committeesTable, committeeId)

    // meetings
    fun getMeetings() = getAll<Meeting>(meetingsTable)
    fun getFutureMeetings(): List<Meeting> =
            getMeetings().filter { it.time > System.currentTimeMillis() }.sortedBy { it.time }

    fun getPastMeetings(): List<Meeting> =
            getMeetings().filter { it.time <= System.currentTimeMillis() }.sortedBy { it.time }

    fun getMeeting(id: Any): Meeting? = get(meetingsTable, id)
    fun insertMeeting(meeting: Meeting) {
        insert(meetingsTable, meeting)

        if (meeting.time >= System.currentTimeMillis()) {
            val committee = meeting.committee!!
            val email = renderHbs(
                    HandlebarsContent(
                            "emails/new-meeting.hbs",
                            mapOf(
                                    "committee" to committee,
                                    "date" to meeting.date,
                                    "location" to meeting.location,
                                    "url" to "$urlBase/meetings/${meeting.meetingId}",
                                    "urlBase" to urlBase
                            )
                    )
            )

            sendMessage(
                    createEmail(
                            meeting.committee!!.committeeMemberships.map { it.member.email },
                            fromEmail,
                            "${committee.formalName} meeting at ${meeting.date}",
                            email
                    )
            )
        }
    }

    fun updateMeeting(meeting: Meeting) = update(meetingsTable, meeting.meetingId, meeting)
    fun deleteMeeting(meetingId: String) = delete(meetingsTable, meetingId)


    // legislation
    fun insertLegislation(legislation: Legislation) {
        insert(legislationTable, legislation)

        val members = legislation.committee.committeeMemberships.map { it.member.email }

        val email = renderHbs(
                HandlebarsContent(
                        "emails/legislation-new.hbs",
                        mapOf(
                                "legislation" to legislation,
                                "url" to "$urlBase/legislation/view/${legislation.id}",
                                "urlBase" to urlBase
                        )
                )
        )

        sendMessage(
                createEmail(
                        members,
                        fromEmail,
                        "New Legislation | ${legislation.name}",
                        email
                )
        )

    }

    fun getLegislation() = getAll<Legislation>(legislationTable)
    fun getLegislation(id: String): Legislation? = get(legislationTable, id)
    fun updateLegislation(legislation: Legislation) {
        update(legislationTable, legislation.id, legislation)

        if (legislation.enacted) {
            val members = legislation.committee.committeeMemberships.map { it.member.email }

            val email = renderHbs(
                    HandlebarsContent(
                            "emails/legislation-enacted.hbs",
                            mapOf(
                                    "legislation" to legislation,
                                    "url" to "$urlBase/legislation/view/${legislation.id}",
                                    "urlBase" to urlBase
                            )
                    )
            )

            sendMessage(
                    createEmail(
                            members,
                            fromEmail,
                            "Legislation Has Been Enacted | ${legislation.name}",
                            email
                    )
            )
        }
    }

    fun deleteLegislation(legislationId: String) = delete(legislationTable, legislationId)
    fun getEnactedLegislation() = getLegislation().filter { it.enacted }
    fun getFailedLegislation() = getLegislation().filter { it.failed }
    fun getPassedLegislation() = getLegislation().filter { it.passed }
    fun getInactiveLegislation() = getLegislation().filter { !it.active }
    fun getActiveLegislation() = getLegislation().filter { it.active }

    // votes
    fun insertVote(vote: Vote) = insert(votesTable, vote)
    fun insertIndividualVote(individualVote: IndividualVote, vote: Vote) =
            updateVote(vote.apply { votes.add(individualVote) })

    fun getVotes() = getAll<Vote>(votesTable)
    fun getVote(id: String): Vote? = get(votesTable, id)
    fun getVotesContainingMember(member: String) =
            getVotes().filter { it.votes.any { vote -> vote.username == member } }

    fun getMemberVotes(member: String) =
            getVotesContainingMember(member).map { it.votes.first { vote -> vote.username == member } }

    fun updateVote(vote: Vote) = update(votesTable, vote.voteId, vote)
    fun deleteVote(voteId: String) = delete(votesTable, voteId)


    // statements
    fun getStatements() = getAll<Statement>(statementsTable).sortedByDescending { it.lastEditTime ?: it.createdAt }
    fun insertStatement(statement: Statement) = insert(statementsTable, statement)
    fun getStatement(id: String): Statement? = get(statementsTable, id)
    fun updateStatement(statement: Statement) = update(statementsTable, statement.id, statement)
    fun deleteStatement(statementId: String) = delete(statementsTable, statementId)

    // messages
    fun getMessage(id: String): Message? = get(messagesTable, id)
    fun updateMessage(message: Message) = update(messagesTable, message.id, message)
    fun updateMessage(id: String, value: Any) = update(messagesTable, id, Message(id, value))
    fun insertMessage(message: Message) = insert(messagesTable, message)
    fun getSpeakerMessage() = getMessage("speaker_message")!!.value
    fun getWhitcombDescription() = getMessage("whitcomb_description")!!.value.toString()

    // dockets
    fun getDecision(id: String): SupremeCourtDecision? = get(supremeCourtDecisions, id)
    fun insertDecision(decision: SupremeCourtDecision) = insert(supremeCourtDecisions, decision)
    fun updateDecision(decision: SupremeCourtDecision) = update(messagesTable, decision.id, decision)
    fun deleteDecision(id: String) = delete(messagesTable, id)

    fun getComplaint(id: String): Complaint? = get(complaintsTable, id)
    fun updateComplaint(complaint: Complaint) = update(complaintsTable, complaint.id, complaint)
    fun deleteComplaint(id: String) = delete(complaintsTable, id)
    fun insertComplaint(complaint: Complaint) = insert(complaintsTable, complaint)

    // utils

    fun getUuid() = r.uuid().run<String>(connection)!!


    fun update(table: String, id: Any, obj: Idable): Any? {
        return r.table(table).get(id).replace(r.json(gson.toJson(obj))).run<Any>(connection)
    }

    fun delete(table: String, id: Any): Any? {
        return r.table(table).get(id).delete().run<Any>(connection)
    }

    fun <T : Idable> insert(table: String, obj: T): Any? {
        return r.table(table).insert(r.json(gson.toJson(obj))).run<Any>(connection)
    }

    inline fun <reified T : Idable> getAll(table: String): List<T> {
        return r.table(table).run<Any>(connection).queryAsArrayList(gson, T::class.java).filterNotNull()
    }

    inline fun <reified T : Idable> get(table: String, id: Any): T? {
        return asPojo(gson, r.table(table).get(id).run(connection), T::class.java)
    }
}