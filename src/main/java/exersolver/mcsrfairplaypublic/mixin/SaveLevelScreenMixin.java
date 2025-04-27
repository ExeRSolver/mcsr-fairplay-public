package exersolver.mcsrfairplaypublic.mixin;

import exersolver.mcsrfairplaypublic.output.BufferedCryptoZipWriter;
import exersolver.mcsrfairplaypublic.InputListener;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SaveLevelScreen.class)
public abstract class SaveLevelScreenMixin extends Screen {

    protected SaveLevelScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/Screen.render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void drawHashHex(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!(this.title instanceof TranslatableText) || !((TranslatableText) this.title).getKey().equals("menu.savingLevel"))
            return;

        BufferedCryptoZipWriter fileWriter = InputListener.getFileWriter();
        if (fileWriter == null)
            return;

        String hash = fileWriter.getHashHex();
        if (hash == null)
            return;

        this.drawCenteredText(matrices, this.textRenderer, StringRenderable.plain(hash), this.width / 2, 90, 16777215);
    }
}
