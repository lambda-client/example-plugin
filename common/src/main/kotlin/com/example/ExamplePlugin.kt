package com.lambda

import com.lambda.Lambda.LOG
import com.lambda.command.CommandManager.commands
import com.lambda.commands.Time
import com.lambda.module.ModuleRegistry.modules
import com.lambda.plugin.api.Plugin
import com.lambda.modules.Messager
import com.lambda.modules.Range

internal object ExamplePlugin : Plugin(
    name = "Example",
    description = "An example plugin",
    version = "1.0.0",
    author = listOf("Soup"),
) {
    override fun load() {
        LOG.info("The Plugin is being loaded...")

        // Register modules
        modules.add(Messager)
        modules.add(Range)

        // Register commands
        commands.add(Time)
    }

    override fun preLoad() {
        LOG.info("The Plugin is being preloaded...")
    }
}
