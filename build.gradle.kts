import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import java.util.*

val modId = property("mod_id").toString()
val modVersion = property("mod_version").toString()
val mavenGroup = property("maven_group").toString()
val minecraftVersion = property("minecraft_version").toString()
val yarnMappings = property("yarn_mappings").toString()

// The next two lines are used to replace the version in the fabric.mod.json and META-INF/*.toml files
// You most likely don't want to touch this
val targets = listOf("META-INF/*.toml", "fabric.mod.json")
val replacements = file("gradle.properties").inputStream().use { stream ->
    Properties().apply { load(stream) }
}.map { (k, v) -> k.toString() to v.toString() }.toMap()

// This is the directory where you can put local libraries that will get indexed by Gradle
val libs = file("libs")

plugins {
    kotlin("jvm") version "1.9.24"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.6-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

architectury {
    minecraft = minecraftVersion
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    dependencies {
        // I'm not sure why we have to use "" here, but it works, if you manage to get it to work without it, please let us know.
        "minecraft"("com.mojang:minecraft:$minecraftVersion")
        "mappings"("net.fabricmc:yarn:$minecraftVersion+$yarnMappings:v2")
    }

    if (path == ":common") return@subprojects

    apply(plugin = "com.github.johnrengelman.shadow")

    val versionWithMCVersion = "$modVersion+$minecraftVersion"

    tasks {
        val shadowCommon by configurations.creating {
            isCanBeConsumed = false
            isCanBeResolved = true
        }

        val shadow = named<ShadowJar>("shadowJar") {
            archiveVersion = versionWithMCVersion
            archiveClassifier = "shadow"
            configurations = listOf(shadowCommon)
        }

        val remapJar = named<RemapJarTask>("remapJar") {
            dependsOn(shadow)
            inputFile = shadow.flatMap { it.archiveFile }
            archiveVersion = versionWithMCVersion
            archiveClassifier = ""
        }

        // This allows us to make the plugin discoverable by Lambda
        val linkJar by creating(Copy::class) {
            dependsOn(remapJar)

            val outputDir = file("/run/mods/")
            outputDir.mkdirs()

            val jarFile = file(
                "build/libs/${modId}-${remapJar.get().archiveFile.get().asFile.name}")

            from(jarFile)
            into(outputDir.toPath())
        }

        build {
            dependsOn(linkJar)
        }

        processResources {
            // This task will replace the placeholders in the fabric.mod.json and META-INF/*.toml files
            filesMatching(targets) {
                expand(replacements)
            }
        }
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = mavenGroup
    version = modVersion

    base.archivesName = modId

    repositories {
        flatDir {
            dirs(libs)
        }
    }

    tasks {
        jar {
            manifest {
                attributes["Main-Class"] = "com.example.ExamplePlugin"
                attributes["Lambda-Plugin"] = "true"
            }
        }
    }

    java {
        withSourcesJar() // This will create a sources jar for your project, allowing you to publish it to Maven repositories

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
