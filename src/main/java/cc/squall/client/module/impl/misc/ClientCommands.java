package cc.squall.client.module.impl.misc;

import cc.squall.client.Client;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleAnnotation;
import cc.squall.client.module.Setting.Setting;
import cc.squall.client.module.Setting.impl.StringSetting;
import cc.squall.client.utils.LWJGL.input.KeyUtil;
import net.minecraft.util.ChatComponentText;

import java.util.Locale;

@ModuleAnnotation
public class ClientCommands extends Module {
    public ClientCommands() {
        super("ClientCommands", "Enables dot commands for client", Category.Misc, false);
    }
    private final StringSetting prefix = new StringSetting("Prefix", this, ".");
    // bad port from nebula but less fat

    @Override
    public void onDotCommand(String command) {
        String[] args = command.substring(1).trim().split("\\s+");

        switch (args[0].toLowerCase()) {
            case "list":
                list();
                break;
            case "enable":
                setState(args, true);
                break;
            case "disable":
                setState(args, false);
                break;
            case "settings":
                settings(args);
                break;
            case "set":
                set(args);
                break;
            case "bind":
                bind(args);
                break;
            default:
                toggle(args[0]);
        }
    }

    private void list() {
        StringBuilder names = new StringBuilder();
        for (Module m : Module.getRegistry()) {
            if (names.length() > 0) names.append(", ");
            names.append(m.getName()).append(m.isEnabled() ? " [on]" : "");
        }
        print(names.toString());
    }

    private void setState(String[] args, boolean enabled) {
        Module m = module(args, 1, ".enable/.disable <module>");
        if (m == null) return;
        m.setEnabled(enabled);
        print(m.getName() + (enabled ? " enabled" : " disabled"));
    }

    private void toggle(String name) {
        Module m = find(name);
        if (m == null) {
            print("unknown command: ." + name + " (list, enable, disable, settings, set, bind, or a module name)");
            return;
        }
        m.toggle();
        print(m.getName() + (m.isEnabled() ? " enabled" : " disabled"));
    }

    private void settings(String[] args) {
        Module m = module(args, 1, ".settings <module>");
        if (m == null) return;
        if (m.getSettings().isEmpty()) {
            print(m.getName() + " has no settings");
            return;
        }
        for (Setting s : m.getSettings()) {
            print(s.getName() + " = " + s.getValueString());
        }
    }

    private void set(String[] args) {
        Module m = module(args, 1, ".set <module> <setting> <value>");
        if (m == null) return;
        if (args.length < 4) {
            print("usage: .set <module> <setting> <value>");
            return;
        }
        for (Setting s : m.getSettings()) {
            if (s.getName().equalsIgnoreCase(args[2])) {
                print(s.setValueString(args[3]) ? s.getName() + " = " + s.getValueString() : "invalid value: " + args[3]);
                return;
            }
        }
        print("setting not found: " + args[2]);
    }

    private void bind(String[] args) {
        if (args.length >= 2 && args[1].equalsIgnoreCase("list")) {
            StringBuilder bound = new StringBuilder();
            for (Module m : Module.getRegistry()) {
                if (m.getKeybind() <= 0) continue;
                if (bound.length() > 0) bound.append(", ");
                bound.append(m.getName()).append(" = ").append(keyName(m.getKeybind()));
            }
            print(bound.length() == 0 ? "nothing is bound" : bound.toString());
            return;
        }

        Module m = module(args, 1, ".bind <module> [key|none]   or   .bind list");
        if (m == null) return;
        if (args.length < 3) {
            print(m.getName() + " is bound to " + (m.getKeybind() <= 0 ? "nothing" : keyName(m.getKeybind())));
            return;
        }

        int key = parseKey(args[2]);
        if (key == -1) {
            print("unknown key: " + args[2] + " (try a name like RSHIFT, a number, or none)");
            return;
        }
        m.setKeybind(key);
        print(m.getName() + " bound to " + (key == 0 ? "nothing" : keyName(key)));
    }

    private static int parseKey(String token) {
        if (token.equalsIgnoreCase("none") || token.equalsIgnoreCase("unbind")) return 0;
        try {
            int raw = Integer.parseInt(token);
            return raw > 0 ? raw : -1;
        } catch (NumberFormatException ignored) {
            return KeyUtil.findKeycode("KEY_" + token.toUpperCase(Locale.ROOT));
        }
    }

    private static String keyName(int code) {
        String name = KeyUtil.findKeyString(code);
        return name == null ? String.valueOf(code) : name.replace("KEY_", "");
    }

    private static Module find(String name) {
        return Client.getInstance().getModman().getByName().get(name.toLowerCase(Locale.ROOT));
    }

    private Module module(String[] args, int index, String usage) {
        if (args.length <= index) {
            print("usage: " + usage);
            return null;
        }
        Module m = find(args[index]);
        if (m == null) print("module not found: " + args[index]);
        return m;
    }

    private void print(String message) {
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
    }
}
