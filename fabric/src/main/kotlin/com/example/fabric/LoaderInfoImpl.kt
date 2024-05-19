package com.example.fabric

import net.fabricmc.loader.api.FabricLoader

object LoaderInfoImpl {
    @JvmStatic
    fun getVersion(): String =
        FabricLoader.getInstance()
            .getModContainer("example").orElseThrow()
            .metadata.version.friendlyString
}
