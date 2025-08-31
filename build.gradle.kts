import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.provideDelegate
import java.util.*

val modId: String by project
val modVersion: String by project
val mavenGroup: String by project
val minecraftVersion: String by project
val lambdaVersion: String by project
val yarnMappings: String by project
val fabricLoaderVersion: String by project
val fabricApiVersion: String by project
val kotlinFabricVersion: String by project
val classGraphVersion: String by project
val kotlinVersion: String by project

// The next lines are used to replace the version in the fabric.mod.json files
// You most likely don't want to touch this
val targets = listOf("fabric.mod.json")
val replacements = file("gradle.properties").inputStream().use { stream ->
    Properties().apply { load(stream) }
}.map { (k, v) -> k.toString() to v.toString() }.toMap()

// This is the directory where you can put local libraries that will get indexed by Gradle
val libs = file("libs")

plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.dokka") version "2.0.0"
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("com.gradleup.shadow") version "9.0.0-rc1"
    id("maven-publish")
}

group = mavenGroup
version = modVersion

base.archivesName = modId

repositories {
    mavenLocal() // Allow the use of local repositories
    maven("https://maven.2b2t.vc/releases") // Baritone
    maven("https://jitpack.io") // KDiscordIPC
    mavenCentral()

    // Allow the use of local libraries
    flatDir {
        dirs(libs)
    }
}

loom {
    accessWidenerPath = file("src/main/resources/$modId.accesswidener")
    enableTransitiveAccessWideners = true
    enableModProvidedJavadoc = true

    runs {
        all {
            programArgs("--username", "Steve", "--uuid", "8667ba71b85a4004af54457a9734eed7", "--accessToken", "<TOKEN>")
        }
    }
}

val includeLib: Configuration by configurations.creating
val includeMod: Configuration by configurations.creating
val shadowLib: Configuration by configurations.creating { isCanBeConsumed = false }
val shadowMod: Configuration by configurations.creating { isCanBeConsumed = false }

fun DependencyHandlerScope.setupConfigurations() {
    includeLib.dependencies.forEach {
        implementation(it)
        include(it)
    }

    includeMod.dependencies.forEach {
        modImplementation(it)
        include(it)
    }

    shadowLib.dependencies.forEach {
        implementation(it)
    }

    shadowMod.dependencies.forEach {
        modImplementation(it)
    }
}

dependencies {
    // Read this if you'd like to understand the gradle dependency hell
    // https://medium.com/@nagendra.raja/understanding-configurations-and-dependencies-in-gradle-ad0827619501

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$minecraftVersion+$yarnMappings:v2")

    // Add Kotlin
    // If you wish to use additional Kotlin features, you can add them here
    // You do not need to include them in the final jar since we are using
    // Kotlin mod loaders which already include them.
    // implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    // Fabric
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion+$minecraftVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$kotlinFabricVersion.$kotlinVersion")

    // Lambda (Do not touch)
    modImplementation("com.lambda:lambda:$lambdaVersion+$minecraftVersion")

    // This is the library we use for reflections
    // You most likely will not need to use it so,
    // you can comment the line or remove it entirely.
    //
    // This library allows us to scan the classpath
    // for classes.
    //
    // If you are new to programming or new to Java
    // you may want to read this article on the classpath:
    // https://medium.com/javarevisited/back-to-the-basics-of-java-part-1-classpath-47cf3f834ff
    implementation("io.github.classgraph:classgraph:${classGraphVersion}")

    // Add mods to the mod jar
    implementation("com.github.rfresh2:baritone-fabric:$minecraftVersion")

    // Finish the configuration
    setupConfigurations()
}

tasks {
    shadowJar {
        archiveClassifier = "dev-shadow"
        archiveVersion = "$modVersion+$minecraftVersion"
        configurations = listOf(shadowLib, shadowMod)
    }

    remapJar {
        dependsOn(shadowJar)

        inputFile = shadowJar.get().archiveFile
        archiveVersion = "$modVersion+$minecraftVersion"
    }

    processResources {
        filesMatching(targets) { expand(replacements) }

        // Forces the task to always run
        outputs.upToDateWhen { false }
    }
}

kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
