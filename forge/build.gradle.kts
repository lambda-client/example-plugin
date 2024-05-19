val lambdaVersion: String by project
val minecraftVersion: String by project
val forgeVersion: String by project
val mixinExtrasVersion: String by project
val kotlinForgeVersion: String by project

base.archivesName = "${base.archivesName.get()}-forge"

architectury {
    platformSetupLoomIde()

    // Tells Architectury to use the Forge mod loader.
    forge()
}

loom {
    accessWidenerPath = project(":common").loom.accessWidenerPath

    forge {
        // This is required to convert the access wideners to the forge
        // format, access transformers.
        convertAccessWideners = true

        // Add the mod's mixins to the list of mixins to be applied.
        // In the extraordinary case that you need to add mixins for
        // different mod loaders, you can add them using the
        // `extraAccessWideners` property.
        // And then add them to the `mixinConfig` function.
        mixinConfig("example.mixins.common.json")
    }
}

repositories {
    // You can add more repositories here if you plan
    // on using environment-specific dependencies.
    // If you simply want to add a global repository,
    // you can add it to the `settings.gradle.kts` file
    // in the base of the project and gradle will do the
    // rest for you.
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentForge"].extendsFrom(this)
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
        forgeRuntimeLibrary(it)
        include(it)
    }

    includeMod.dependencies.forEach {
        forgeRuntimeLibrary(it)
        include(it)
    }

    shadowBundle.dependencies.forEach {
        shadowCommon(it)
        shadow(it)
    }
}

dependencies {
    // Forge API (Do not touch)
    forge("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    // Lambda (Do not touch)
    // The dependency below, except for lambda forge, are REQUIRED
    // to launch the game inside the development environment.
    modImplementation("com.lambda:lambda-forge-$lambdaVersion+$minecraftVersion") { isTransitive = false }
    modImplementation("thedarkcolour:kotlinforforge:$kotlinForgeVersion") // FixMe: Stupid forge
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

    // MixinExtras is a library that adds useful mixins
    // for making complex mods easier to develop.
    implementation("io.github.llamalad7:mixinextras-forge:$mixinExtrasVersion")
    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:$mixinExtrasVersion")!!)

    // Common (Do not touch)
    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":common", configuration = "transformProductionForge"))

    // Finish the configuration
    setupConfigurations()
}
