package exersolver.mcsrfairplaypublic.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import exersolver.mcsrfairplaypublic.InputListener;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @WrapOperation(method = "lockCursor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V"))
    private void onCursorLock(long handler, int value, double x, double y, Operation<Void> original) {
        InputListener.onCursorLockChanged(true, (int) x, (int) y);
        original.call(handler, value, x, y);
    }

    @WrapOperation(method = "unlockCursor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V"))
    private void onCursorUnlock(long handler, int value, double x, double y, Operation<Void> original) {
        InputListener.onCursorLockChanged(false, (int) x, (int) y);
        original.call(handler, value, x, y);
    }
}
