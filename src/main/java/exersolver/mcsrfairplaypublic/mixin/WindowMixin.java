package exersolver.mcsrfairplaypublic.mixin;

import exersolver.mcsrfairplaypublic.InputListener;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {

    @Inject(
            method = "onWindowPosChanged",
            at = @At("HEAD")
    )
    private void onWindowPosChanged(long window, int x, int y, CallbackInfo ci) {
        InputListener.onWindowPosChanged(x, y);
    }

    @Inject(
            method = "onWindowSizeChanged",
            at = @At("HEAD")
    )
    private void onWindowSizeChanged(long window, int width, int height, CallbackInfo ci) {
        InputListener.onWindowSizeChanged(width, height);
    }
}
