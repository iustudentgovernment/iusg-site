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
val mainClassPath = "edu.indiana.iusg.InspireTransitionKt"

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("com.github.jknack:handlebars:4.1.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(project(":shared"))
    implementation("com.google.guava:guava:29.0-jre")
    implementation("com.github.jknack:handlebars-guava-cache:4.1.2")

}

tasks {
    processResources {
        dependsOn(":frontend:browserWebpack")
        from(project(":frontend").projectDir.resolve("src/main/resources")) {
            into("static")
        }
        from(project(":frontend").buildDir.resolve("distributions/frontend.js")) {
            into("static")
        }
    }

    register("stage") {
        dependsOn(jar)
        dependsOn(processResources)
        dependsOn(installDist)
    }
}

application {
    mainClassName = mainClassPath
}

