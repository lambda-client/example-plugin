package com.soup.can.modules

import com.lambda.module.Module
import com.lambda.module.tag.ModuleTag
import com.lambda.util.Communication.info
import com.lambda.util.text.*
import java.awt.Color

object Messager : Module(
    name = "Messager",
    description = "A module that sends messages in the game",
    defaultTags = setOf(ModuleTag.CLIENT),
) {
    init {
        onEnable {
            val text = buildText {
                literal("Hello, this is a message from the Messager module")
                clickEvent(ClickEvents.openUrl("https://www.youtube.com/watch?v=RvVdFXOFcjw")) {
                    color(Color.GREEN) {
                        literal(" (Click me!)")
                    }
                }
            }

            info(text)
        }
    }
}
