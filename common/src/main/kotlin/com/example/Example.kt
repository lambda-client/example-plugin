package com.example

import com.example.commands.Time
import com.example.modules.Messager
import com.example.modules.Range
import com.lambda.command.CommandRegistry
import com.lambda.core.registry.AgnosticRegistries
import com.lambda.module.ModuleRegistry
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import com.mojang.blaze3d.systems.RenderSystem.recordRenderCall
import net.minecraft.sound.SoundEvent

object Example {
    const val MOD_NAME = "Example"
    const val MOD_ID = "example"
    val VERSION: String = LoaderInfo.getVersion()
    val LOG: Logger = LogManager.getLogger(MOD_NAME)

    fun initialize() {
        // This is required to interact with the Lambda core.
        // Without this you are initializing your mod outside a safe OpenGL context.
        recordRenderCall {
            ModuleRegistry.modules.add(Messager)
            ModuleRegistry.modules.add(Range)

            CommandRegistry.commands.add(Time)

            // Example of how to register custom resources
            // The RegistryController is an object that allows you to register custom resources
            // on any loaders.
            // Once they are dumped into their respective registries, you cannot add more.
            AgnosticRegistries.register(Registries.SOUND_EVENT, Identifier(MOD_ID, "example_sound"), SoundEvent.of(Identifier(MOD_ID, "example_sound")))

            LOG.info("Plugin $MOD_NAME $VERSION initialized.")
        }
    }
}
