package dev.lukebemish.letmemixinplease.test.mixin.embeddium;

import org.embeddedt.embeddium.impl.Embeddium;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Embeddium.class)
@Pseudo
public class EmbeddiumTest {
    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void onClinit(CallbackInfo ci) {
        System.out.println("Successfully injected into otherwise protected embeddium class!");
    }
}
