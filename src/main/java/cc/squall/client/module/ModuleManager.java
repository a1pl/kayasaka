package cc.squall.client.module;

import cc.squall.client.Client;
import cc.squall.client.events.*;
import cc.squall.client.events.Input.Mouse.*;
import cc.squall.client.events.pLaYeR.*;
import cc.squall.client.events.netowrking.*;
import cc.squall.client.events.Update.EventRenderUpdate;
import cc.squall.client.events.Update.EventRender3dUpdate;
import cc.squall.client.events.Input.Keyboard.EventKeyPress;
import cc.squall.client.events.Input.Keyboard.EventKeyRelease;
import cc.squall.client.events.Update.EventRender2dUpdate;
import cc.squall.client.events.Update.EventUpdate;
import cc.squall.client.module.Setting.Setting;
import cc.squall.client.module.Setting.impl.EnumSetting;
import cc.squall.client.module.Setting.impl.ListSetting;
import cc.squall.client.module.Setting.impl.ModeSetting;
import cc.squall.client.module.Setting.impl.SliderSetting;
import cc.squall.client.module.Setting.impl.StringSetting;
import cc.squall.client.module.Setting.impl.ToggleSetting;
import cc.squall.client.module.generated.GeneratedModules;
import cc.squall.client.utils.datatypes.lists.GlueList;
import lombok.Getter;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter
public class ModuleManager {
    @Getter
    private final Map<Class<? extends Module>, Module> byClass = new HashMap<>();
    @Getter
    private final Map<String, Module> byName = new HashMap<>();
    @Getter
    private final Map<Module.Category, List<Module>> byCategory = new EnumMap<>(Module.Category.class);

    public ModuleManager() {
        for (Module.Category c : Module.Category.values()) {
            byCategory.put(c, new GlueList<>());
        }
    }

    public void init() {
        GeneratedModules.registerAll();

        for (Module m : Module.getRegistry()) {
            byClass.put(m.getClass(), m);
            byName.put(m.getName().toLowerCase(Locale.ROOT), m);
            byCategory.get(m.getCategory()).add(m);
            m.onLoad();
        }

        MethodEventShim shim = Client.getInstance().getShim();
        shim.subscribe(EventUpdate.class, this::onUpdate);
        shim.subscribe(EventRender2dUpdate.class, this::onRender2dUpdate);
        shim.subscribe(EventKeyPress.class, this::onKeyPress);
        shim.subscribe(EventKeyRelease.class, this::onKeyRelease);
        shim.subscribe(EventDotCommand.class, this::onDotCommand);
        shim.subscribe(EventRenderItem.class, this::onRenderItem);
        shim.subscribe(EventSwingAnimation.class, this::onSwingAnimation);
        shim.subscribe(EventMouseClick.class, this::onMouseClick);
        shim.subscribe(EventMouseRelease.class, this::onMouseRelease);
        shim.subscribe(EventMouseLeftClick.class, this::onMouseLeftClick);
        shim.subscribe(EventMouseLeftRelease.class, this::onMouseLeftRelease);
        shim.subscribe(EventMouseRightClick.class, this::onMouseRightClick);
        shim.subscribe(EventMouseRightRelease.class, this::onMouseRightRelease);
        shim.subscribe(EventMouseMove.class, this::onMouseMove);
        shim.subscribe(EventRenderUpdate.class, this::onRenderUpdate);
        shim.subscribe(EventRender3dUpdate.class, this::onRender3dUpdate);
        shim.subscribe(EventPacketSend.class, this::onPacketSend);
        shim.subscribe(EventPacketReceive.class, this::onPacketReceive);
        shim.subscribe(EventPlayerMove.class, this::onPlayerMove);
        shim.subscribe(EventPlayerMotion.class, this::onPlayerMotion);
        shim.subscribe(EventPlayerLook.class, this::onPlayerLook);
        shim.subscribe(EventPlayerStrafe.class, this::onPlayerStrafe);
        shim.subscribe(EventPostPlayerStrafe.class, this::onPostPlayerStrafe);

    }
    public void enable(Module m) {
        m.setEnabled(true);
    }

    public void disable(Module M) {
        M.setDisabled(true);
    }
    public void toggle(Module mda) {
        mda.toggle();
    }

    public boolean isEnabled(String name) {
        Module m = byName.get(name.toLowerCase(Locale.ROOT));
        return m != null && m.isEnabled();
    }
    public boolean isDisabled(String name) {
        return !isEnabled(name);
    }

    public List<Module> getModules(Module.Category category) {
        return byCategory.get(category);
    }
    public List<Module> getModules() {
        return Module.getRegistry();
    }

    // events
    public void onUpdate(EventUpdate event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onUpdate(event);
            }
        }
    }

    public void onRender2dUpdate(EventRender2dUpdate event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onRender2dUpdate(event);
            }
        }
    }
    //keybinds here too
    public void onKeyPress(EventKeyPress event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onKeyPress(event);
            }
        }

        for (Module m : Module.getRegistry()) {

            if (m.getKeybind() <= 0 || event.getKeyCode() != m.getKeybind()) {
                continue;
            }
            if (m.getKeybindType() == Module.KeybindType.TOGGLE) {
                m.toggle();
            } else if (m.getKeybindType() == Module.KeybindType.TOGGLECONTROL) {
                m.toggle();
                if (m.isEnabled()) {
                    // if loop slop, case switch is evil
                    m.onKeyPress(event);
                }
            } else if (m.getKeybindType() == Module.KeybindType.HOLD) {
                m.setEnabled(true);
            } else if (m.getKeybindType() == Module.KeybindType.HOLDCONTROL) {
                m.setEnabled(true);
                m.onKeyPress(event);
            }
        }
    }
    public void onDotCommand(EventDotCommand event) {
        for (Module m :Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onDotCommand(event.getCommand());
            }
        }
    }
    public void onKeyRelease(EventKeyRelease event) {
        for (Module m : Module.getRegistry()) {
            if (m.getKeybind() <= 0 || event.getKeyCode() != m.getKeybind()) {
                continue;
            }

            if (m.getKeybindType() == Module.KeybindType.HOLD) {
                m.setEnabled(false);
            } else if (m.getKeybindType() == Module.KeybindType.HOLDCONTROL) {
                m.setEnabled(false);
                m.onKeyRelease(event);
            } else if (m.getKeybindType() == Module.KeybindType.TOGGLECONTROL && m.isEnabled()) {
                m.onKeyRelease(event);
            }
        }
    }
    public void onSwingAnimation(EventSwingAnimation event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onSwingAnimation(event);
                if (event.isCancelled()) return;
            }
        }
    }
    public void onRenderItem(EventRenderItem event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onRenderItem(event);
                // a module that cancelled did the transform itself, stacking another would double it
                if (event.isCancelled()) return;
            }
        }
    }

    public void onMouseClick(EventMouseClick event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onMouseClick(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onMouseRelease(EventMouseRelease event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onMouseRelease(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onMouseLeftClick(EventMouseLeftClick event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onMouseLeftClick(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onMouseLeftRelease(EventMouseLeftRelease event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onMouseLeftRelease(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onMouseRightClick(EventMouseRightClick event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onMouseRightClick(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onMouseRightRelease(EventMouseRightRelease event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onMouseRightRelease(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onMouseMove(EventMouseMove event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onMouseMove(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onRenderUpdate(EventRenderUpdate event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onRenderUpdate(event);
                if (event.isCancelled()) return;
            }
        }
    }

    // TODO: uhh add more when i make more
    public static String getSettingType(Setting setting) {
        if (setting instanceof EnumSetting) return "Enum";
        if (setting instanceof ModeSetting) return "Mode";
        if (setting instanceof ListSetting) return "List";
        if (setting instanceof SliderSetting) return "Slider";
        if (setting instanceof StringSetting) return "Text";
        if (setting instanceof ToggleSetting) return "Toggle";
        return "Unknown";
    }

    public void onPacketSend(EventPacketSend event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onPacketSend(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onPlayerMove(EventPlayerMove event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onPlayerMove(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onPlayerMotion(EventPlayerMotion event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onPlayerMotion(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onPlayerLook(EventPlayerLook event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onPlayerLook(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onPlayerStrafe(EventPlayerStrafe event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onPlayerStrafe(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onPostPlayerStrafe(EventPostPlayerStrafe event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onPostPlayerStrafe(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onPacketReceive(EventPacketReceive event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onPacketReceive(event);
                if (event.isCancelled()) return;
            }
        }
    }

    public void onRender3dUpdate(EventRender3dUpdate event) {
        for (Module m : Module.getRegistry()) {
            if (m.isEnabled()) {
                m.onRender3dUpdate(event);
                if (event.isCancelled()) return;
            }
        }
    }
}
