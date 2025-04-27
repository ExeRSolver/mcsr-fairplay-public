package exersolver.mcsrfairplaypublic.mixin;

import exersolver.mcsrfairplaypublic.InputListener;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Shadow public abstract String getTranslationKey();

    @Inject(at = @At("HEAD"), method = "setBoundKey")
    private void onKeyBindingChanged(InputUtil.Key boundKey, CallbackInfo ci) {
        InputListener.onKeyBindingChanged(this.getTranslationKey(), boundKey);
    }
}
