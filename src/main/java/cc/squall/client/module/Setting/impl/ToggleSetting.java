package cc.squall.client.module.Setting.impl;

import cc.squall.client.module.Module;
import cc.squall.client.module.Setting.Setting;
import lombok.Getter;
import lombok.Setter;

public class ToggleSetting extends Setting {
    @Getter
    @Setter
    private boolean toggled;
    public ToggleSetting(String namex, Module ownerx, boolean toggled) {
        super(namex, ownerx);
        this.toggled = toggled;
    }

    @Override
    public String getValueString() {
        return Boolean.toString(toggled);
    }

    @Override
    public boolean setValueString(String value) {
        if (value == null) return false;
        String v = value.trim();
        if (v.equalsIgnoreCase("true"))  { toggled = true;  return true; }
        if (v.equalsIgnoreCase("false")) { toggled = false; return true; }
        return false;
    }

    public boolean getValue() {
        return toggled;
    }
    public void setValue(boolean x) {
        toggled = x;
    }
    public void toggle() { toggled = !toggled; }
}
