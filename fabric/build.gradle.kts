plugins {
    alias(libs.plugins.loom)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.resourcefactory)
}

description = "A mod that converts NBS files to DiamondFire code templates"

dependencies {
    minecraft(libs.minecraft)
    mappings(libs.yarn)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.language.kotlin)
    modImplementation(libs.adventure.platform.fabric)

    implementation(libs.adventure.api)
    implementation(project(":common"))
}

fabricModJson {
    id = "nbs3df"
    clientEntrypoint("codes.reason.nbs3df.mod.NBS3DFClientInitializer")
    depends("fabric-language-kotlin", "*")
    depends("minecraft", libs.versions.minecraft.get())

    author("Reasonless") {
        contact.sources = "https://github.com/Reasonless/"
    }
}
