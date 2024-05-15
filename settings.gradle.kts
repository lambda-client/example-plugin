rootProject.name = "ExamplePlugin"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        mavenCentral()
        gradlePluginPortal()
    }
}

include("common")
include("fabric") // Only used for the mod discovery
include("forge") // Only used for the mod discovery

