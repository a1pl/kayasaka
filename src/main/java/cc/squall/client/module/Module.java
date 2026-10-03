package cc.squall.client.module;

import cc.squall.client.Client;
import cc.squall.client.events.*;
import cc.squall.client.events.Input.Mouse.*;
import cc.squall.client.events.pLaYeR.*;
import cc.squall.client.events.netowrking.*;
import cc.squall.client.events.Input.Keyboard.EventKeyPress;
import cc.squall.client.events.Input.Keyboard.EventKeyRelease;
import cc.squall.client.events.Update.EventRender2dUpdate;
import cc.squall.client.events.Update.EventRenderUpdate;
import cc.squall.client.events.Update.EventRender3dUpdate;
import cc.squall.client.events.Update.EventUpdate;
import cc.squall.client.module.Setting.Setting;
import cc.squall.client.utils.datatypes.lists.GlueList;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    @Getter
    protected final Minecraft mc = Client.getInstance().getMc();
    @Getter
    private static final List<Module> registry = new GlueList<>();
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final KeybindType keybindType;

    @Getter
    private final Category category;
    @Getter
    @Setter
    private boolean hidden;

    @Getter
    private final ArrayList<Setting> settings = new ArrayList<>();
    @Getter
    public final EntityPlayerSP player = mc.thePlayer;
    public void onDotCommand(String command) {}
    @Getter
    private boolean enabled;
    @Setter @Getter private int keybind = -1;
    public void onEnable() {}
    public void onDisable() {}

    public void setEnabled(boolean enabled) {
        // REALLY confusing but works trust
        if (this.enabled == enabled){
            return;
        }
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        }
        else {
            onDisable();
        }
    }

    public void setDisabled(boolean disabled) {
        setEnabled(!disabled);
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public Module(String name, String description, Category category, boolean hidden, KeybindType keybindType) {
        this.name = name;
        this.description = description;
        this.keybindType = keybindType;
        this.category = category;
        this.hidden = hidden;
        this.enabled = false;
        this.keybind = 0;
        registry.add(this);
    }
    public Module(String name, String description, Category category, KeybindType keybindType) {
        this.name = name;
        this.description = description;
        this.keybindType = keybindType;
        this.category = category;
        this.hidden = false;
        this.enabled = false;
        this.keybind = 0;
        registry.add(this);
    }
    public Module(String name, String description, Category category, boolean hidden) {
        this.name = name;
        this.description = description;
        this.keybindType = KeybindType.TOGGLE;
        this.category = category;
        this.hidden = hidden;
        this.enabled = false;
        this.keybind = 0;
        registry.add(this);
    }
    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.keybindType = KeybindType.TOGGLE;
        this.category = category;
        this.hidden = false;
        this.enabled = false;
        this.keybind = 0;
        registry.add(this);
    }

    public void setVisible(boolean visible) {
        this.hidden = !visible;
    }
    public void onLoad() {}
    public void onUnload() {}
// events
    public void onKeyPress(EventKeyPress event) {

    }
    public void onUpdate(EventUpdate event) {

    }

    public void onKeyRelease(EventKeyRelease event) {
    }
    public void onRender2dUpdate(EventRender2dUpdate event) {
    }

    public void onRenderUpdate(EventRenderUpdate event) {
    }

    public void onRender3dUpdate(EventRender3dUpdate event) {
    }

    public void onPacketSend(EventPacketSend event) {
    }

    public void onPacketReceive(EventPacketReceive event) {
    }

    public void onPlayerMove(EventPlayerMove event) {
    }

    public void onPlayerMotion(EventPlayerMotion event) {
    }

    public void onPlayerLook(EventPlayerLook event) {
    }

    public void onPlayerStrafe(EventPlayerStrafe event) {
    }

    public void onPostPlayerStrafe(EventPostPlayerStrafe event) {
    }

    public void onSwingAnimation(EventSwingAnimation event) {
    }

    public void onRenderItem(EventRenderItem event) {
    }

    public void onMouseClick(EventMouseClick event) {
    }

    public void onMouseRelease(EventMouseRelease event) {
    }

    public void onMouseLeftClick(EventMouseLeftClick event) {
    }

    public void onMouseLeftRelease(EventMouseLeftRelease event) {
    }

    public void onMouseRightClick(EventMouseRightClick event) {
    }

    public void onMouseRightRelease(EventMouseRightRelease event) {
    }

    public void onMouseMove(EventMouseMove event) {
    }

    public enum Category {
        Combat, Movement, Render, Player, Misc
    }
    public enum KeybindType {
        HOLD,
        TOGGLE,
        HOLDCONTROL, // probably never gonna get used
        TOGGLECONTROL // same with this one
    }
}
