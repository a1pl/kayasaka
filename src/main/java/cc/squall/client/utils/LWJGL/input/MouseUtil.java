package cc.squall.client.utils.LWJGL.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjglx.input.Mouse;

public class MouseUtil {

    public static float getX() {
        Minecraft mc = Minecraft.getMinecraft();
        return (float) Mouse.getX() * new ScaledResolution(mc).getScaledWidth() / mc.displayWidth;
    }

    public static float getY() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        return sr.getScaledHeight() - (float) Mouse.getY() * sr.getScaledHeight() / mc.displayHeight - 1;
    }
}
