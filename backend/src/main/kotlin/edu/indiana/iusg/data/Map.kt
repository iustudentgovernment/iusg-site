package edu.indiana.iusg.data


fun getMap(pageTitle: String, pageId: String, pageDescription: String? = null): MutableMap<String, Any?> {
    val map = mutableMapOf<String, Any?>()

    // head
    map["siteTitle"] = "Inspire IUSG Transition"
    map["siteDescription"] = "The Inspire IUSG ticket will take office May 1, 2020. Rachel + Ruhan will be your new Student Body President and Vice President. Get involved here!"
    map["pageDescription"] = pageDescription

    // nav

    // page
    map["pageTitle"] = "${map["siteTitle"]}: $pageTitle"
    map["pageId"] = pageId
    return map
}