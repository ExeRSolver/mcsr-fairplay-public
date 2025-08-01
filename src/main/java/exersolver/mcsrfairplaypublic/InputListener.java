package exersolver.mcsrfairplaypublic;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeSystem;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import exersolver.mcsrfairplaypublic.output.BufferedCryptoZipWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputListener implements NativeMouseInputListener, NativeMouseWheelListener, NativeKeyListener {
    private static BufferedCryptoZipWriter fileWriter = null;

    public static void setFileWriter(BufferedCryptoZipWriter fileWriterIn) {
        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                MCSRFairplayPublic.LOGGER.error(e.getMessage(), e);
            }
        }
        fileWriter = fileWriterIn;

        String modVersion = "";
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(MCSRFairplayPublic.MOD_ID);
        if (modContainer.isPresent())
            modVersion = "-" + modContainer.get().getMetadata().getVersion().getFriendlyString();
        fileWriter.log(System.nanoTime(), "Created log file for " + MCSRFairplayPublic.MOD_ID + modVersion);

        modContainer = FabricLoader.getInstance().getModContainer("minecraft");
        modContainer.ifPresent(container -> fileWriter.log("Minecraft version: " + container.getMetadata().getVersion().getFriendlyString()));

        fileWriter.log("Operating system: " + System.getProperty("os.name").toLowerCase(Locale.ROOT));

        Window window = MinecraftClient.getInstance().getWindow();
        fileWriter.log(String.format("window pos %d %d", window.getX(), window.getY()));
        fileWriter.log(String.format("window size %d %d", window.getWidth(), window.getHeight()));

        fileWriter.log("KeyRepeatDelay: " + System.getProperty("jnativehook.key.repeat.delay"));
        fileWriter.log("KeyRepeatRate: " + System.getProperty("jnativehook.key.repeat.rate"));

        if (NativeSystem.getFamily().equals(NativeSystem.Family.WINDOWS)) {
            String key = "Control Panel\\Accessibility\\Keyboard Response";

            fileWriter.log("AutoRepeatDelay: " + Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, key, "AutoRepeatDelay"));
            fileWriter.log("AutoRepeatRate: " + Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, key, "AutoRepeatRate"));
            fileWriter.log("BounceTime: " + Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, key, "BounceTime"));
            fileWriter.log("DelayBeforeAcceptance: " + Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, key, "DelayBeforeAcceptance"));
            fileWriter.log("Flags: " + Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, key, "Flags"));
        }

        KeyBinding[] keyMappings = MinecraftClient.getInstance().options.keysAll;
        for (KeyBinding keyMapping : keyMappings) {
            fileWriter.log(keyMapping.getTranslationKey() + ":" + keyMapping.getBoundKeyTranslationKey());
        }
        fileWriter.log("LOG START");
    }

    public static void init() {
        if (System.getProperty("os.name").toLowerCase().startsWith("mac"))
            return;

        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setUseParentHandlers(false);
        Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            MCSRFairplayPublic.LOGGER.error(e.getMessage(), e);
        }

        GlobalScreen.addNativeMouseMotionListener(new InputListener());
        GlobalScreen.addNativeMouseListener(new InputListener());
        GlobalScreen.addNativeMouseWheelListener(new InputListener());
        GlobalScreen.addNativeKeyListener(new InputListener());
    }

    public static void closeFileWriter() {
        if (fileWriter == null || fileWriter.closed)
            return;

        try {
            fileWriter.close();
            String hash = fileWriter.getHashHex();
            MCSRFairplayPublic.LOGGER.info(fileWriter.getHashFileName() + ": " + hash);
        } catch (IOException e) {
            MCSRFairplayPublic.LOGGER.error(e.getMessage(), e);
        }
    }

    public static void onFocusChanged(boolean focused) {
        long time = System.nanoTime();
        if (fileWriter == null)
            return;

        fileWriter.log(time, String.format("focused %b", focused));
    }

    public static void onWindowPosChanged(int x, int y) {
        long time = System.nanoTime();
        if (fileWriter == null)
            return;

        fileWriter.log(time, String.format("window pos %d %d", x, y));
    }

    public static void onWindowSizeChanged(int width, int height) {
        long time = System.nanoTime();
        if (fileWriter == null)
            return;

        fileWriter.log(time, String.format("window size %d %d", width, height));
    }

    public static void onCursorLockChanged(boolean locked, int x, int y) {
        long time = System.nanoTime();
        if (fileWriter == null)
            return;

        fileWriter.log(time, String.format("cursor %s %d %d", locked ? "locked" : "unlocked", x, y));
    }

    public static void onKeyBindingChanged(String name, InputUtil.Key boundKey) {
        long time = System.nanoTime();
        if (fileWriter == null)
            return;

        fileWriter.log(time, String.format("keyBind %s:%s", name, boundKey.getTranslationKey()));
    }

    public static void onScreenChanged(Screen screen) {
        long time = System.nanoTime();
        if (fileWriter == null)
            return;

        String screenText = screen == null ? "null" : screen.getClass().getName();
        fileWriter.log(time, String.format("screen %s", screenText));
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        long time = System.nanoTime();
        if (fileWriter == null || !MinecraftClient.getInstance().isWindowFocused())
            return;

        fileWriter.log(time, String.format("cursor %d %d", nativeMouseEvent.getX(), nativeMouseEvent.getY()));
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
        long time = System.nanoTime();
        if (fileWriter == null || !MinecraftClient.getInstance().isWindowFocused())
            return;

        fileWriter.log(time, String.format("cursor %d %d", nativeMouseEvent.getX(), nativeMouseEvent.getY()));
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
        long time = System.nanoTime();
        if (fileWriter == null || !MinecraftClient.getInstance().isWindowFocused())
            return;

        fileWriter.log(time, String.format("mouseButton pressed %d", nativeMouseEvent.getButton()));
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        long time = System.nanoTime();
        if (fileWriter == null || !MinecraftClient.getInstance().isWindowFocused())
            return;

        fileWriter.log(time, String.format("mouseButton released %d", nativeMouseEvent.getButton()));
    }

    @Override
    public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent) {
        long time = System.nanoTime();
        if (fileWriter == null || !MinecraftClient.getInstance().isWindowFocused())
            return;

        fileWriter.log(time, String.format("mouseWheel scrolled %d", nativeMouseWheelEvent.getWheelRotation()));
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        long time = System.nanoTime();
        if (fileWriter == null || !MinecraftClient.getInstance().isWindowFocused())
            return;

        fileWriter.log(time, String.format("key typed %c [%d] (%d,%d)", nativeKeyEvent.getKeyChar(), nativeKeyEvent.getModifiers(), (int) nativeKeyEvent.getKeyChar(), nativeKeyEvent.getRawCode()));
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        long time = System.nanoTime();
        if (fileWriter == null || !MinecraftClient.getInstance().isWindowFocused())
            return;

        fileWriter.log(time, String.format("key pressed \"%s\" [%d] (%d)", NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()), nativeKeyEvent.getModifiers(), nativeKeyEvent.getRawCode()));
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        long time = System.nanoTime();
        if (fileWriter == null || !MinecraftClient.getInstance().isWindowFocused())
            return;

        fileWriter.log(time, String.format("key released \"%s\" [%d] (%d)", NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()), nativeKeyEvent.getModifiers(), nativeKeyEvent.getRawCode()));
    }
}