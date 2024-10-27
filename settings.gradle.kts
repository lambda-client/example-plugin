rootProject.name = "ExamplePlugin"

pluginManagement {
    repositories {
        mavenLocal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        mavenCentral()
        gradlePluginPortal()
    }
}

include("common") // This is your real project
include("fabric") // Only used for the mod discovery
include("forge") // Only used for the mod discovery

