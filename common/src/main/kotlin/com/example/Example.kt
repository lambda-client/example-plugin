package com.example

import com.lambda.command.CommandRegistry
import com.lambda.module.ModuleRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Example {
    private const val MOD_NAME = "Example"
    const val MOD_ID = "example"

    private val VERSION: String = LoaderInfo.getVersion()
    private val LOG: Logger = LogManager.getLogger(MOD_NAME)

    fun initialize() {
        // Injecting into Lambda's registries
        ModuleRegistry.injectPath("com.example.modules")
        CommandRegistry.injectPath("com.example.commands")

        LOG.info("Plugin $MOD_NAME $VERSION initialized.")
    }
}
