package edu.indiana.iustudentgovernment.utils

import com.google.api.client.util.Base64.encodeBase64URLSafeString
import com.google.api.services.gmail.model.Message
import edu.indiana.iustudentgovernment.emailTest
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Properties
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Multipart
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

/*
private const val APPLICATION_NAME = "Gmail API Java Quickstart"
private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
private const val TOKENS_DIRECTORY_PATH = "tokens"

private val SCOPES: List<String> = GmailScopes.all().toList()
private const val CREDENTIALS_FILE_PATH = "/credentials.json"

val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()!!
var service = Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
    .setApplicationName(APPLICATION_NAME)
    .build()!!
const val userId = "me"

private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential? {
    val inputStream = Congress::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
        ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
    val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

    // Build flow and trigger user authorization request.
    val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build()

    val receiver = LocalServerReceiver.Builder().setPort(8888).build()
    return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
}*/

fun createEmail(
    toList: List<String>,
    from: String,
    subject: String,
    bodyText: String
): MimeMessage? {
    val props = Properties()
    val session: Session = Session.getDefaultInstance(props, null)
    val email = MimeMessage(session)
    email.setFrom(InternetAddress(from))
    email.addRecipients(
        javax.mail.Message.RecipientType.TO,
        (if (emailTest) listOf("aratzman2@gmail.com") else toList).map { InternetAddress(it) }.toTypedArray()
    )
    email.subject = subject
    email.setContent(bodyText, "text/html")
    return email
}

fun createMessageWithEmail(emailContent: MimeMessage): Message? {
    val buffer = ByteArrayOutputStream()
    emailContent.writeTo(buffer)
    val bytes = buffer.toByteArray()
    val encodedEmail: String = encodeBase64URLSafeString(bytes)
    val message = Message()
    message.raw = encodedEmail
    return message
}

fun createEmailWithAttachment(
    to: String?,
    from: String?,
    subject: String?,
    bodyText: String?,
    file: File
): MimeMessage? {
    val props = Properties()
    val session = Session.getDefaultInstance(props, null)
    val email = MimeMessage(session)
    email.setFrom(InternetAddress(from))
    email.addRecipient(
        javax.mail.Message.RecipientType.TO,
        InternetAddress(to)
    )
    email.subject = subject
    var mimeBodyPart = MimeBodyPart()
    mimeBodyPart.setContent(bodyText, "text/html")
    val multipart: Multipart = MimeMultipart()
    multipart.addBodyPart(mimeBodyPart)
    mimeBodyPart = MimeBodyPart()
    val source = FileDataSource(file)
    mimeBodyPart.dataHandler = DataHandler(source)
    mimeBodyPart.fileName = file.name
    multipart.addBodyPart(mimeBodyPart)
    email.setContent(multipart)
    return email
}

fun sendMessage(
    emailContent: MimeMessage?
): Message? {
    if (!emailTest) {
        //var message = createMessageWithEmail(emailContent!!)
        //message = service.users().messages().send(userId, message).execute()
        //return message
    }
    return null
}
