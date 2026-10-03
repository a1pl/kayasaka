package cc.squall.client.utils.render.utils.rendering.blur;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class MenuBlur {
    public void load() {
        Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/menu_blur.json"));
    }
    public void unload() {
        Minecraft.getMinecraft().entityRenderer.stopUseShader();
    }
}
