package cc.squall.client.module.impl.combat;

import cc.squall.client.Client;
import cc.squall.client.events.Update.EventUpdate;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleAnnotation;
import cc.squall.client.module.Setting.impl.ToggleSetting;

@ModuleAnnotation
public class DelayRemover extends Module{
    public DelayRemover() {
        super("DelayRemover", "Removes various delays", Module.Category.Combat, false);
    }
    private final ToggleSetting hitDelay = new ToggleSetting("1.7 Hitreg", this, true);
    private final ToggleSetting jumpDelayFix = new ToggleSetting("Jump", this, false);
    private final ToggleSetting mouseDelayFix = new ToggleSetting("Mouse", this, true);

    @Override
    public void onUpdate(EventUpdate event) {
        if (hitDelay.isToggled()) {
            Client.getInstance().getMc().setLeftClickCounter(0);
        }
        if (jumpDelayFix.isToggled()) {
            Client.getInstance().getMc().thePlayer.setJumpTicks(0);
        }
        Client.getInstance().getMc().thePlayer.setMouseDelayFix(mouseDelayFix.isToggled());
    }
}
