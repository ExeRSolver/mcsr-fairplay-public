package exersolver.mcsrfairplaypublic.mixin;

import exersolver.mcsrfairplaypublic.output.OutputUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow
    private MinecraftClient client;

    @Inject(
            method = "onGameJoin",
            at = @At("TAIL")
    )
    public void onWorldJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        MinecraftServer server = this.client.getServer();
        if (server != null)
            OutputUtils.setFileWriter(server.getSavePath(WorldSavePath.ROOT).getParent());
    }
}
