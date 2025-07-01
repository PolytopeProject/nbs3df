plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.loom) apply false
}

allprojects {
    group = "codes.reason"
    version = "1.0-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
    }
}
//
//kotlin {
//    js(IR) {
//        browser {
//            commonWebpackConfig {
//                cssSupport {
//                    enabled = true
//                }
//            }
//        }
//        binaries.executable()
//
//        compilations["main"].dependencies {
//            implementation(npm("@popperjs/core", "2.11.8"))
//            implementation(npm("bootstrap", "5.3.7"))
//            implementation(npm("bootstrap-icons", "1.11.0"))
//            implementation(npm("pako", "2.1.0"))
//            devNpm("style-loader", "3.3.1")
//            devNpm("css-loader", "6.8.1")
//        }
//    }
//    sourceSets {
//        val jsMain by getting {
//            dependencies {
//                implementation(libs.serialization.json)
//                implementation(libs.coroutines.core)
//                implementation(libs.compose.html.core)
//                implementation(libs.compose.html.svg)
//                implementation(libs.compose.runtime)
//                implementation(project(":common"))
//            }
//        }
//    }
//}