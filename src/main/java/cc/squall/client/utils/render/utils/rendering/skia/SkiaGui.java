package cc.squall.client.utils.render.utils.rendering.skia;

import io.github.humbleui.skija.Canvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

import java.util.function.Consumer;

public final class SkiaGui {

    private SkiaGui() {}

    // skiarenderer wrapper for scaledresolution
    public static void draw(ScaledResolution sr, Consumer<Canvas> drawer) {
        Minecraft mc = Minecraft.getMinecraft();
        int width = mc.displayWidth;
        int height = mc.displayHeight;

        Framebuffer framebuffer = mc.getFramebuffer();
        if (OpenGlHelper.isFramebufferEnabled() && framebuffer != null) {
            width = framebuffer.framebufferTextureWidth;
            height = framebuffer.framebufferTextureHeight;
        }

        SkiaRenderer.draw(width, height, sr.getScaleFactor(), drawer);
    }
}
