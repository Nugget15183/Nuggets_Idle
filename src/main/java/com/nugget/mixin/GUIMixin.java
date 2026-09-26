package com.nugget.mixin;

import com.nugget.NuggetSIdle;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GUIMixin {
    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void extractRenderState(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci) {
        if (NuggetSIdle.hideGUI) {
            ci.cancel();
        }
    }
}
