package edu.indiana.iusg.http

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.Template
import com.github.jknack.handlebars.cache.GuavaTemplateCache
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import com.github.jknack.handlebars.io.TemplateSource
import com.google.common.cache.CacheBuilder
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import java.util.concurrent.TimeUnit.MINUTES

private val handlebars = instantiateHandlebars()

private fun instantiateHandlebars(): Handlebars {
    val hb = Handlebars(ClassPathTemplateLoader().apply {
        prefix = "/templates/"
        suffix = ""
    })

    registerHelpers(hb)

    val cache = CacheBuilder.newBuilder().expireAfterWrite(10, MINUTES)
            .maximumSize(1000).build<TemplateSource, Template>()

    return hb.with(GuavaTemplateCache(cache))
}

private fun registerHelpers(handlebars: Handlebars) {
    handlebars.registerHelper("ifeq") { first: Any?, options: Options ->
        if (options.params[0].toString().equals(first?.toString(), true)) {
            options.fn()
        } else options.inverse()
    }
}

private fun render(content: HandlebarsContent): String {
    val viewName: String = content.viewName
    val template = handlebars.compile(viewName)
    return template.apply(content.model)
}

data class HandlebarsContent(val viewName: String, val model: Map<String, Any?>)

suspend fun ApplicationCall.respondHbs(handlebarsContent: HandlebarsContent, status: HttpStatusCode?=null) {
        respondText(render(handlebarsContent), ContentType.parse("text/html"), status)
}