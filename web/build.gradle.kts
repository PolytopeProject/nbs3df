plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
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
                implementation(libs.coroutines.core)
                implementation(libs.serialization.json)
                implementation(libs.compose.html.core)
                implementation(libs.compose.html.svg)
                implementation(libs.compose.runtime)
                implementation(project(":common"))
            }
        }
    }
}

tasks.named<Copy>("jsProcessResources") {
    from(rootProject.path + "df-sound-mappings") {
        include("minecraft_to_df.json")
    }
}