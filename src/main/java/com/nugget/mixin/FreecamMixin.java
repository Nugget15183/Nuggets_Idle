package com.nugget.mixin;

import com.nugget.NuggetSIdle;
import net.xolt.freecam.config.ModBindings;
import net.xolt.freecam.config.keys.FreecamKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FreecamKeyMapping.class)
public class FreecamMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo ci) {
        if (NuggetSIdle.camcontroller.isEnabled()) {
            ci.cancel();
        }
    }
}