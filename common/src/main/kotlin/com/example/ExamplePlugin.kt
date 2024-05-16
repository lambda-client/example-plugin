package com.example

import com.lambda.Lambda.LOG
import com.lambda.command.CommandManager.commands
import com.lambda.module.ModuleRegistry.modules
import com.lambda.plugin.api.Plugin
import com.example.modules.Messager
import com.example.modules.Range
import com.example.commands.Time

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
}
