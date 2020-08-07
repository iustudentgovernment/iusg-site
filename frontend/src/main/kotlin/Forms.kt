import org.w3c.dom.HTMLFormElement
import org.w3c.dom.asList
import kotlin.browser.document
import kotlin.dom.addClass

fun formErrorCheck() {
    val url = document.URL
    val split = url.split("?")
    if (split.size == 1) return
    split[1].split("&").forEach { keyValue ->
        val (key, value) = keyValue.split("=")
        if (key == "error") {
            setError(value)
        }
    }
}

fun setError(text: String, buttonId: String? = null) {
    document.getElementById("error-text")!!.innerHTML = text
    document.getElementById("error-box")!!.removeAttribute("hidden")
    buttonId?.let { document.getElementById(it)?.setAttribute("value", "Scroll up to fix errors.") }
}

fun unsetBorderCollapseTable() {
    document.getElementsByTagName("table").asList().forEach { element ->
        element.addClass("unset-border")
    }
}

fun unsetBgColor() {
    document.getElementsByTagName("tr").asList().forEach { element ->
        element.addClass("remove-bg")
    }
}

fun validateJobSubmission() {
    val name = document.getElementById("name")!!.asDynamic().value.toString()
    val email = document.getElementById("email")!!.asDynamic().value.toString()
    val confirmEmail = document.getElementById("confirmemail")!!.asDynamic().value.toString()

    if (name.isBlank()) {
        setError("Please enter your name.", "submit-app")
        return
    }
    if (email.isBlank() || !email.toString().contains(".edu") || !email.toString().contains("@")) {
        setError("Please enter your email.", "submit-app")
        return
    }

    if (confirmEmail.isBlank()) {
        setError("Please confirm your email.", "submit-app")
        return
    }

    if (confirmEmail != email) {
        setError("Your emails didn't match.", "submit-app")
        return
    }


    val years = document.getElementsByTagName("input").asList()
            .filter { it.getAttribute("name")?.startsWith("year") == true }
            .map { it.asDynamic() }

    if (years.none { it.checked == true }) {
        setError("You need to select a class year.", "submit-app")
        return
    }

    val schools = document.getElementsByTagName("input").asList()
            .filter { it.getAttribute("name")?.startsWith("school") == true }
            .map { it.asDynamic() }

    if (schools.none { it.checked == true }) {
        setError("You need to select at least one school.", "submit-app")
        return
    }

    val whyJoin = document.getElementById("whyjoin")!!.asDynamic().value.toString()
    val whyRole = document.getElementById("whyrole")!!.asDynamic().value.toString()
    if (whyJoin.isBlank() || whyRole.isBlank()) {
        setError("Please answer all short answer questions.", "submit-app")
        return
    }

    (document.getElementById("application")!! as HTMLFormElement).submit()
}