package com.soup.can.modules

import com.lambda.event.events.TickEvent
import com.lambda.event.listener.SafeListener.Companion.listener
import com.lambda.module.Module
import com.lambda.module.tag.ModuleTag
import com.lambda.util.world.WorldUtils.getFastEntities
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity

object Range : Module(
    name = "Range",
    description = "Print all the entities in a range",
    defaultTags = setOf(ModuleTag.WORLD),
) {
    private enum class Mode(val value: Class<out Entity>) {
        All(Entity::class.java),
        Mobs(HostileEntity::class.java),
        Players(PlayerEntity::class.java),
        Animals(AnimalEntity::class.java),
    }

    private val mode by setting("Mode", Mode.All)
    private val range by setting("Range", 10.0, 1.0..32.0, 1.0)

    private val iterator: (Entity, Int) -> Unit = { entity, _ ->
        // This property was accessed using the Access Wideners
        // How to access other properties?
        //
        // Intellij:
        // - Go to the definition of the property
        // - Right click on the property
        // - Click on "Copy / Paste Special"
        // - Click on "AW Reference"
        // - Paste the reference in the access widener file
        // - Click the refresh arrows in the Gradle tool window
        val onFire = entity.hasVisualFire
        println(entity)
    }

    init {
        listener<TickEvent.Pre> {
            getFastEntities(mode.value, player.pos, range, iterator = iterator)
        }
    }
}
