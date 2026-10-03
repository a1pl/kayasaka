package cc.squall.client.module.Setting.impl;

import cc.squall.client.module.Module;
import cc.squall.client.module.Setting.Setting;

public class StringSetting extends Setting {
    String string;
    public StringSetting(String namex, Module ownerx, String string) {
        super(namex, ownerx);
        this.string = string;
    }

    @Override
    public String getValueString() {
        return string;
    }

    @Override
    public boolean setValueString(String value) {
        string = value;
        return true;
    }
    public boolean is(String value) {
        return string.equalsIgnoreCase(value);
    }
}
