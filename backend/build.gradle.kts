plugins {
    kotlin("jvm")
    id("idea")
    id("application")
}

repositories {
    jcenter()
    mavenCentral()
}

val ktorVersion = "1.3.1"
val mainClassPath = "edu.indiana.iustudentgovernment.IUSGSiteKt"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("com.github.jknack:handlebars:4.1.2")
    implementation("com.google.guava:guava:29.0-jre")
    implementation("com.github.jknack:handlebars-guava-cache:4.1.2")
    implementation("org.jsoup:jsoup:1.12.1")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.rethinkdb:rethinkdb-driver:2.3.3")
    implementation("com.atlassian.commonmark:commonmark-ext-gfm-tables:0.15.2")

    // gmail
    implementation("com.google.api-client:google-api-client:1.30.7")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.30.4")
    implementation("com.google.apis:google-api-services-gmail:v1-rev105-1.25.0")

    implementation("com.sun.mail:javax.mail:1.6.2")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(project(":shared"))

}

tasks {
    register("stage") {
        dependsOn(jar)
        dependsOn(processResources)
        dependsOn(installDist)
    }
}

application {
    mainClassName = mainClassPath
}

