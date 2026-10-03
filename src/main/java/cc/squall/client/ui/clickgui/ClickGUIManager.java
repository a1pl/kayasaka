package cc.squall.client.ui.clickgui;

import cc.squall.client.Client;
import cc.squall.client.events.Input.Keyboard.EventKeyPress;
import cc.squall.client.events.Update.EventRender2dUpdate;
import cc.squall.client.events.Update.EventUpdate;
import cc.squall.client.events.Update.EventRenderUpdate;
import cc.squall.client.events.MethodEventShim;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleManager;
import cc.squall.client.module.Setting.Setting;
import cc.squall.client.module.Setting.impl.ListSetting;
import cc.squall.client.module.Setting.impl.ModeSetting;
import cc.squall.client.module.Setting.impl.SliderSetting;
import cc.squall.client.module.Setting.impl.ToggleSetting;
import cc.squall.client.ui.clickgui.panel.components.SettingComponents.ListComponent;
import cc.squall.client.ui.clickgui.panel.components.SettingComponents.SliderComponent;
import cc.squall.client.ui.clickgui.panel.components.SettingComponents.ToggleComponent;
import cc.squall.client.module.Setting.impl.EnumSetting;
import cc.squall.client.module.Setting.impl.StringSetting;
import cc.squall.client.ui.clickgui.panel.PanelClickGUI;
import cc.squall.client.ui.clickgui.panel.components.ModuleComponent;
import cc.squall.client.ui.clickgui.panel.components.Panel;
import cc.squall.client.ui.clickgui.panel.components.SettingComponents.*;
import cc.squall.client.utils.LWJGL.input.KeyUtil;
import cc.squall.client.events.Input.Mouse.EventMouseClick;
import cc.squall.client.events.Input.Mouse.EventMouseMove;
import cc.squall.client.events.Input.Mouse.EventMouseRelease;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Objects;

public class ClickGUIManager {
    public ArrayList<ClickGUI> list = new ArrayList<ClickGUI>();
    public ClickGUIManager() {
        list.add(new PanelClickGUI());
    }
    public ClickGUI current;
    public void init() {
        MethodEventShim shim = Client.getInstance().getShim();
        shim.subscribe(EventRender2dUpdate.class, this::onRender2d);
        shim.subscribe(EventUpdate.class, this::onUpdate);
        shim.subscribe(EventKeyPress.class, this::onKeyPress);
        shim.subscribe(EventMouseClick.class, this::onMouseClick);
        shim.subscribe(EventMouseRelease.class, this::onMouseRelease);
        shim.subscribe(EventMouseMove.class, this::onMouseMove);
        shim.subscribe(EventRenderUpdate.class, this::onRenderUpdate);
        current = list.get(0);

        for (ClickGUI c : list) {
            if (!(c instanceof PanelClickGUI)) continue;
            PanelClickGUI gui = (PanelClickGUI) c;
            // walk the panels themselves, so there is no lookup that can miss
            for (Panel pan : gui.getPanels()) {
                for (Module module : Client.getInstance().getModman().getModules(pan.getCategory())) {
                    ModuleComponent modcomp = new ModuleComponent(module);
                    for (Setting x : module.getSettings()) {
                        switch (ModuleManager.getSettingType(x)) {
                            case "Enum":
                                modcomp.add(new ModeComponent(((EnumSetting<?>) x).getMode()));
                                break;
                            case "Mode":
                                modcomp.add(new ModeComponent((ModeSetting) x));
                                break;
                            case "List":
                                modcomp.add(new ListComponent((ListSetting) x));
                                break;
                            case "Slider":
                                modcomp.add(new SliderComponent((SliderSetting) x));
                                break;
                            case "Text":
                                modcomp.add(new StringComponent((StringSetting) x));
                                break;
                            case "Toggle":
                                modcomp.add(new ToggleComponent((ToggleSetting) x));
                                break;
                            default:
                                break;
                        }
                    }

                    pan.add(modcomp);
                }
            }
        }
    }

    public boolean isAnyShown() {
        for (ClickGUI c : list) {
            if (c.isShown()) return true;
        }
        return false;
    }

    public void onRender2d(EventRender2dUpdate event) {
        for (ClickGUI c : list) {
            if (c.isShown()) {
                c.onRender2d(event);
            }
        }
    }
    public void onUpdate(EventUpdate event) {
        for (ClickGUI c : list) {
            if (c.isShown()) {
                Minecraft.getMinecraft().setIngameNotInFocus();
            }
            c.onUpdate();
        }
    }
    public void onKeyPress(EventKeyPress event) {
        if (Objects.equals(KeyUtil.findKeyString(event.getKeyCode()), "KEY_RSHIFT")) {
            for (ClickGUI c : list) {
                if (c == current) {
                    c.toggle();
                }
            }
        }
    }

    public void onMouseClick(EventMouseClick event) {
        for (ClickGUI c : list) {
            if (c.isShown()) {
                    c.onMouseClick(event);
            }
        }
    }

    public void onMouseRelease(EventMouseRelease event) {
        for (ClickGUI c : list) {
            if (c.isShown()) {
                c.onMouseRelease(event);
            }
        }
    }

    public void onMouseMove(EventMouseMove event) {
        for (ClickGUI c : list) {
            if (c.isShown()) {
                c.onMouseMove(event);
            }
        }
    }

    public void onRenderUpdate(EventRenderUpdate event) {
        for (ClickGUI c : list) {
            if (c.isShown()) {
                c.onRenderUpdate();
            }
        }
    }
}
