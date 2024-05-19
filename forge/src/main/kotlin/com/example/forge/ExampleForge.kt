package com.example.forge

import com.example.Example
import net.minecraftforge.fml.common.Mod

@Mod(Example.MOD_ID)
object ExampleForge {
    init { Example.initialize() }
}
