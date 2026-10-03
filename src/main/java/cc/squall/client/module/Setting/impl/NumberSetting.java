package cc.squall.client.module.Setting.impl;

import cc.squall.client.module.Module;
import cc.squall.client.module.Setting.Setting;

public class NumberSetting extends Setting {
    int x;
    public NumberSetting(String namex, Module ownerx, int value) {
        super(namex, ownerx);
        this.x = value;
    }

    @Override
    public String getValueString() {
        return "" + x;
    }

    @Override
    public boolean setValueString(String value) {
        x = Integer.parseInt(value);
        return true;
    }
    public boolean isString(String value) {
        String y = "" + x;
        return y.equalsIgnoreCase(value);
    }
    public boolean is(int Value) {
        return x == Value;
    }
}
