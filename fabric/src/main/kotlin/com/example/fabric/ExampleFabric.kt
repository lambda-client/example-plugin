package com.example.fabric

import com.example.Example
import net.fabricmc.api.ClientModInitializer

object ExampleFabric : ClientModInitializer {
    override fun onInitializeClient() = Example.initialize()
}
