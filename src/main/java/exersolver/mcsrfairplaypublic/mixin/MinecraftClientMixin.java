package exersolver.mcsrfairplaypublic.mixin;

import exersolver.mcsrfairplaypublic.InputListener;
import exersolver.mcsrfairplaypublic.output.OutputUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow
	@Nullable
	public ClientWorld world;

	@Inject(at = @At("HEAD"), method = "method_29607")
	public void onCreate(String worldName, LevelInfo levelInfo, RegistryTracker.Modifiable registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
		if (MinecraftClient.getInstance().isOnThread())
			OutputUtils.setFileWriter(worldName);
    }

	@Inject(at = @At("HEAD"), method = "startIntegratedServer(Ljava/lang/String;)V")
	public void onWorldOpen(String worldName, CallbackInfo ci) {
		OutputUtils.setFileWriter(worldName);
	}

	@Inject(at = @At("HEAD"), method = "onWindowFocusChanged(Z)V")
	private void onFocusChanged(boolean focused, CallbackInfo info) {
		InputListener.onFocusChanged(focused);
	}

	@Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
	public void disconnect(CallbackInfo ci) {
		if (this.world != null)
			InputListener.closeFileWriter();
	}

	@Inject(
			at = @At(value = "FIELD",
					target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
					opcode = org.objectweb.asm.Opcodes.PUTFIELD),
			method = "openScreen"
	)
	private void onScreenChanged(Screen screen, CallbackInfo ci) {
		InputListener.onScreenChanged(screen);
	}
}