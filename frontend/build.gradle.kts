plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

kotlin {
    target {
        browser {
            dceTask {
                keep(
                        "iusg-transition-frontend.formErrorCheck",
                        "iusg-transition-frontend.unsetBorderCollapseTable",
                        "iusg-transition-frontend.unsetBgColor",
                        "iusg-transition-frontend.validateJobSubmission",

                        "frontend.formErrorCheck",
                        "frontend.unsetBorderCollapseTable",
                        "frontend.unsetBgColor",
                        "frontend.validateJobSubmission",

                        "iusg-transition-frontend.formErrorCheck",
                        "iusg-transition-frontend.unsetBorderCollapseTable",
                        "iusg-transition-frontend.unsetBgColor",
                        "iusg-transition-frontend.validateJobSubmission"
                )
            }
        }

        sourceSets {
            main {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                    implementation(project(":shared"))
                }
            }
        }
    }
}