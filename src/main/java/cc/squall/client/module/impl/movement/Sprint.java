package cc.squall.client.module.impl.movement;

import cc.squall.client.events.Update.EventUpdate;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleAnnotation;

@ModuleAnnotation
public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Sprints automatically", Category.Movement);
    }
    @Override
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer == null) return;
        if (!mc.gameSettings.keyBindSprint.isKeyDown()) {
            mc.gameSettings.keyBindSprint.setPressed(true);
        }
    }
}
