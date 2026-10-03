package cc.squall.client.ui.clickgui;

import cc.squall.client.events.Input.Mouse.EventMouseClick;
import cc.squall.client.events.Input.Mouse.EventMouseMove;
import cc.squall.client.events.Input.Mouse.EventMouseRelease;
import cc.squall.client.events.Update.EventRender2dUpdate;
import lombok.Getter;
import net.minecraft.client.Minecraft;

public abstract class ClickGUI {
    @Getter
    private final String name;
    @Getter
    private boolean shown = false;
    public ClickGUI(String name) {
        this.name = name;
    }

    public void toggle() {
        shown = !shown;
        if (shown) {
            Minecraft.getMinecraft().setIngameNotInFocus();
            onShow();
        } else {
            Minecraft.getMinecraft().setIngameFocus();
            onHide();
        }
        onToggle();
    }

    public void onUpdate() {}
    public void onRenderUpdate() {}
    public void onRender2d(EventRender2dUpdate event) {
    }
    public void onHide() {}
    public void onShow() {}
    public void onToggle() {}
    public void onMouseClick(EventMouseClick event) {}
    public void onMouseRelease(EventMouseRelease event) {}
    public void onMouseMove(EventMouseMove event) {}

}
