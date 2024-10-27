val lambdaVersion: String by project
val minecraftVersion: String by project
val forgeVersion: String by project

base.archivesName = "${base.archivesName.get()}-forge"

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
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
    // If you simply want to add a global plugin repository,
    // you can add it to the `settings.gradle.kts` file
    // in the base of the project and gradle will do the
    // rest for you.
    // If you want to add more global repositories, you can
    // add them to the root build.gradle.kts file.
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

val common: Configuration by configurations.creating {
    configurations.compileClasspath.get().extendsFrom(this)
    configurations.runtimeClasspath.get().extendsFrom(this)
    configurations["developmentForge"].extendsFrom(this)
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
    // Forge API (Do not touch)
    forge("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")

    // Lambda (Do not touch)
    modImplementation("com.lambda:lambda-forge:$lambdaVersion+$minecraftVersion")

    // Add dependencies on the required Kotlin modules.
    // includeLib(...)

    // Add mods to the mod jar
    // includeMod(...)

    // Common (Do not touch)
    common(project(":common", configuration = "namedElements")) { isTransitive = false }
    shadowBundle(project(":common", configuration = "transformProductionForge")) { isTransitive = false }

    // Finish the configuration
    setupConfigurations()
}
