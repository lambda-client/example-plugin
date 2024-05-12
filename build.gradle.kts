val fabricLoaderVersion = property("fabric_loader_version").toString()
val minecraftVersion = property("minecraft_version").toString()
val yarnMappings = property("yarn_mappings").toString()
val lambdaVersion = property("lambda_version").toString()
val fabricApiVersion = property("fabric_api_version").toString()
val kotlinFabricVersion = property("kotlin_fabric_version").toString()
val mixinExtrasVersion = property("mixinextras_version").toString()

plugins {
    kotlin("jvm") version "1.9.24"
    id("fabric-loom") version "1.6-SNAPSHOT" // Used for Minecraft mappings
}

apply(plugin = "java")
apply(plugin = "org.jetbrains.kotlin.jvm")

group = "com.soup.can"
version = "1.0-SNAPSHOT"

val libs = file("libs")

repositories {
    mavenCentral()

    flatDir {
        dirs(libs)
    }
}

// Include a Non-MC library inside the final jar
val includeLib: Configuration by configurations.creating

// Include a MC library inside the final jar
val includeMod: Configuration by configurations.creating

fun DependencyHandlerScope.setupConfigurations() {
    includeLib.dependencies.forEach {
        implementation(it)
        include(it)
    }

    includeMod.dependencies.forEach {
        implementation(it)
        include(it)
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$minecraftVersion+$yarnMappings:v2")

    // We depend on fabric loader here to use the fabric @Environment annotations and get the mixin dependencies
    // You CANNOT use classes from the fabric loader
    // as they are not available at runtime
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    // Lambda (Do not touch, do not use, do not look at)
    // The dependency below, except for lambda fabric, are REQUIRED
    // to launch the game inside the development environment.
    // They are not included in the final jar.
    modImplementation("com.lambda:lambda-fabric-$lambdaVersion+$minecraftVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion+$minecraftVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$kotlinFabricVersion")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.javassist:javassist:3.28.0-GA")

    // Add dependencies on the required Kotlin modules.
    // They will be included in the final jar.
    // includeLib(...)
    //
    // Example:
    // includeLib("org.reflections:reflections:0.10.2")

    // Add mods to the mod jar
    // They will be included in the final jar.
    // includeMod(...)
    //
    // Example:
    // includeMod("baritone-api:baritone-unoptimized-fabric:1.10.2")

    // Add Kotlin
    implementation(kotlin("stdlib"))

    // Extra Mixins
    implementation("io.github.llamalad7:mixinextras-forge:$mixinExtrasVersion")
}

loom.accessWidenerPath = file("src/main/resources/example.accesswidener") // TODO

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "com.lambda.ExamplePlugin"
        }
    }

    /*create<Copy>("copyPlugin") {
        dependsOn("build")

        // This will export the plugin to the plugins folder
        // This is only for development purposes
        from(buildFile) {
            into("/run/lambda/plugins")
        }
    }*/
}
