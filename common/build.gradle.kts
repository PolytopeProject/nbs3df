plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    js(IR) {
        browser()
    }
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.serialization.json)
                implementation(libs.coroutines.core)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("pako", "2.1.0"))
            }
        }

        val jvmMain by getting {
            dependencies { }
        }
    }
}
