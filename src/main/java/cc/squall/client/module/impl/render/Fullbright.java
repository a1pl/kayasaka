package cc.squall.client.module.impl.render;

import cc.squall.client.events.Update.EventUpdate;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleAnnotation;

@ModuleAnnotation
public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", "Increases brightness", Category.Render, false);
    }
    float original = mc.gameSettings.gammaSetting;
    @Override
    public void onEnable() {
        mc.gameSettings.gammaSetting = 1000.0F;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = original;
    }
}
