plugins {
    kotlin("multiplatform") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("org.jetbrains.compose") version "1.7.0-alpha03"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

group = "codes.reason"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled = true
                }
            }
        }
        binaries.executable()

        compilations["main"].dependencies {
            implementation(npm("@popperjs/core", "2.11.8"))
            implementation(npm("bootstrap", "5.3.7"))
            implementation(npm("bootstrap-icons", "1.11.0"))
            implementation(npm("pako", "2.1.0"))
            devNpm("style-loader", "3.3.1")
            devNpm("css-loader", "6.8.1")
        }
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1") // or latest
                implementation(compose.html.core)
                implementation(compose.html.svg)
                implementation(compose.runtime)

            }
        }
    }
}
tasks {
    named<Copy>("jsProcessResources") {
        from("df-sound-mappings") {
            include("minecraft_to_df.json")
        }
    }

}

