val lambdaVersion: String by project
val modVersion: String by project
val minecraftVersion: String by project
val fabricLoaderVersion: String by project

base.archivesName = "${base.archivesName.get()}-fabric"

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath = project(":common").loom.accessWidenerPath
    enableTransitiveAccessWideners = true
}

repositories {
    // You can add more repositories here if you plan
    // on using environment-specific dependencies.
    // If you simply want to add a global plugin repository,
    // you can add it to the `settings.gradle.kts` file
    // in the base of the project and gradle will do the
    // rest for you.
    // maven(...)
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentFabric"].extendsFrom(this)
    isCanBeResolved = true
    isCanBeConsumed = false
}

// Include a non-minecraft library in the final jar
val includeLib: Configuration by configurations.creating

// Include a mod in the final jar
val includeMod: Configuration by configurations.creating

// The shadow bundle is the final jar that is produced by the shadow plugin.
val shadowBundle: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

fun DependencyHandlerScope.setupConfigurations() {
    includeLib.dependencies.forEach {
        implementation(it)
        include(it)
    }

    includeMod.dependencies.forEach {
        modImplementation(it)
        include(it)
    }
}

dependencies {
    // Fabric API (Do not touch)
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    // Lambda (Do not touch)
    modImplementation("com.lambda:lambda-fabric:$lambdaVersion+$minecraftVersion")

    // Add dependencies on the required Kotlin modules.
    // includeLib(...)

    // Add mods to the mod jar
    // includeMod(...)

    // Common (Do not touch)
    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":common", configuration = "transformProductionFabric")) { isTransitive = false }

    // Finish the configuration
    setupConfigurations()
}

tasks {
    shadowJar {
        archiveVersion = "$modVersion+$minecraftVersion"
        configurations = listOf(shadowBundle)
        archiveClassifier = "dev-shadow"
    }

    remapJar {
        dependsOn(shadowJar)

        archiveVersion = "$modVersion+$minecraftVersion"
        inputFile = shadowJar.get().archiveFile

        // Access wideners are the successor of the mixins accessor
        // that were used in the past to access private fields and methods.
        // They allow you to make field, method, and class access public.
        injectAccessWidener = true
    }
}

