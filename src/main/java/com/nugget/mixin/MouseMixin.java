package com.nugget.mixin;

import com.nugget.NuggetSIdle;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MouseHandler;

@Mixin(MouseHandler.class)
public class MouseMixin {

    private void attemptIdleCancel() {
        NuggetSIdle.lastInput=NuggetSIdle.curTick;
        if(NuggetSIdle.camcontroller.isEnabled()) {
            NuggetSIdle.camcontroller.toggle(false);
        }
    }

    @Inject(method = "onMove", at = @At("HEAD"))
    private void onMouseMove(long handle, double xpos, double ypos, double xrel, double yrel, CallbackInfo ci) {
        attemptIdleCancel();
    }

    @Inject(method = "onScroll", at = @At("HEAD"))
    private void onScroll(long handle, double xoffset, double yoffset, CallbackInfo ci) {
        attemptIdleCancel();
    }

    @Inject(method = "onButton", at = @At("HEAD"))
    private void onButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
        attemptIdleCancel();
    }
}
