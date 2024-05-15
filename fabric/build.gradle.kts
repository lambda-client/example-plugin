val lambdaVersion = property("lambda_version").toString()
val minecraftVersion = property("minecraft_version").toString()
val fabricLoaderVersion = property("fabric_loader_version").toString()
val fabricApiVersion = property("fabric_api_version").toString()
val kotlinFabricVersion = property("kotlin_fabric_version").toString()

base.archivesName = "${base.archivesName.get()}-fabric"

architectury {
    platformSetupLoomIde()

    // Tells Architectury to use the Fabric mod loader.
    fabric()
}

loom {
    accessWidenerPath = project(":common").loom.accessWidenerPath
    enableTransitiveAccessWideners = true
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentFabric"].extendsFrom(this)
    isCanBeResolved = true
    isCanBeConsumed = false
}

// Include a Non-MC library inside the final jar
val includeLib: Configuration by configurations.creating

// Include an MC library inside the final jar
val includeMod: Configuration by configurations.creating

// Include a library inside the final jar
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

    shadowBundle.dependencies.forEach {
        shadowCommon(it)
        shadow(it)
    }
}

dependencies {
    // Fabric API (Do not touch)
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    // Lambda (Do not touch)
    // The dependency below, except for lambda fabric, are REQUIRED
    // to launch the game inside the development environment.
    modImplementation("com.lambda:lambda-fabric-$lambdaVersion+$minecraftVersion") { isTransitive = false }
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion+$minecraftVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:$kotlinFabricVersion")
    implementation("org.reflections:reflections:0.10.2")

    // Add dependencies on the required Kotlin modules.
    // includeLib(...)
    //
    // Example:
    // includeLib("org.reflections:reflections:0.10.2")

    // Add mods to the mod jar
    // includeMod(...)
    //
    // Example:
    // includeMod("baritone-api:baritone-unoptimized-fabric:1.10.2")

    // Common (Do not touch)
    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":common", configuration = "transformProductionFabric"))

    // Finish the configuration
    setupConfigurations()
}

tasks {
    remapJar {
        // Access wideners are the successor of the mixins accessor
        // that were used in the past to access private fields and methods.
        // They allow you to make field, method, and class access public.
        injectAccessWidener = true
    }
}
