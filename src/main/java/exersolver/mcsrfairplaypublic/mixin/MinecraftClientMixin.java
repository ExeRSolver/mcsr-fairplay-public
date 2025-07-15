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

	@Inject(
			method = "method_29607",
			at = @At("HEAD")
	)
	public void onCreate(String worldName, LevelInfo levelInfo, RegistryTracker.Modifiable registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
		if (MinecraftClient.getInstance().isOnThread())
			OutputUtils.setFileWriter(worldName);
    }

	@Inject(
			method = "startIntegratedServer(Ljava/lang/String;)V",
			at = @At("HEAD")
	)
	public void onWorldOpen(String worldName, CallbackInfo ci) {
		OutputUtils.setFileWriter(worldName);
	}

	@Inject(
			method = "onWindowFocusChanged(Z)V",
			at = @At("HEAD")
	)
	private void onFocusChanged(boolean focused, CallbackInfo info) {
		InputListener.onFocusChanged(focused);
	}

	@Inject(
			method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
			at = @At("HEAD")
	)
	public void disconnect(CallbackInfo ci) {
		if (this.world != null)
			InputListener.closeFileWriter();
	}

	@Inject(
			method = "openScreen",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
					opcode = org.objectweb.asm.Opcodes.PUTFIELD
			)
	)
	private void onScreenChanged(Screen screen, CallbackInfo ci) {
		InputListener.onScreenChanged(screen);
	}
}