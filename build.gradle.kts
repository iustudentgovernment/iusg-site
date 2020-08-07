allprojects {
    group = "com.adamratzman"
    version = "1.0.0-SNAPSHOT"
}

plugins {
    val kotlinVersion = "1.3.71"

    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("js") version kotlinVersion apply false
}

tasks {
    register("stage") {
        dependsOn(project("backend").tasks.getByName("stage"))
    }
}