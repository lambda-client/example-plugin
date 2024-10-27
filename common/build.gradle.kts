val modId: String by project
val fabricLoaderVersion: String by project
val kotlinVersion: String by project
val lambdaVersion: String by project
val minecraftVersion: String by project

architectury { common("fabric", "forge") }

loom {
    silentMojangMappingsLicense()
    accessWidenerPath = File("src/main/resources/$modId.accesswidener")
}

dependencies {
    // We depend on fabric loader here to use the fabric
    // @Environment annotations and get the mixin dependencies
    // You CANNOT use classes from the fabric loader
    // as they are not available at runtime
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")

    // Lambda (Do not touch)
    modApi("com.lambda:lambda:$lambdaVersion+$minecraftVersion")

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
    // implementation("org.reflections:reflections:0.10.2")

    // Add Kotlin
    // If you wish to use additional Kotlin features, you can add them here
    // You do not need to include them in the final jar since we are using
    // Kotlin mod loaders which already include them.
    // implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}
