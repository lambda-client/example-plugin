package com.lambda;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    void onTickPre(CallbackInfo ci) {
        System.out.println("MinecraftMixin.onTickPre");
    }

    @Inject(method = "getWindowTitle()Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    private void windowTitle(CallbackInfoReturnable<String> cir) {
        System.out.println("MinecraftMixin.windowTitle");
        cir.setReturnValue("Minecraft - SoupCan");
    }
}
