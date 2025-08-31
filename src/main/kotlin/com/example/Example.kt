package com.example

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Example : ClientModInitializer {
    const val MOD_NAME = "Example"
    const val MOD_ID = "example"
    const val SYMBOL = "λ"

    val VERSION: String = FabricLoader.getInstance()
        .getModContainer(MOD_ID).orElseThrow()
        .metadata.version.friendlyString

    val LOG: Logger = LogManager.getLogger(SYMBOL)

    // noop, the runtime classpath analyzer will load your modules, commands, etc
    override fun onInitializeClient() = LOG.info("Plugin $MOD_NAME $VERSION initialized.")
}
