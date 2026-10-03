package cc.squall.client.module.Setting.impl;

import cc.squall.client.module.Module;
import cc.squall.client.module.Setting.Setting;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ListSetting extends Setting {
    @Getter
    private final List<String> values = new ArrayList<>();

    public ListSetting(String namex, Module ownerx, String... initial) {
        super(namex, ownerx);
        for (String v : initial) add(v);
    }

    public boolean add(String value) {
        if (value == null) return false;
        String v = value.trim();
        if (v.isEmpty() || contains(v)) return false;
        values.add(v);
        return true;
    }

    public boolean remove(String value) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).equalsIgnoreCase(value)) {
                values.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean contains(String value) {
        for (String v : values) {
            if (v.equalsIgnoreCase(value)) return true;
        }
        return false;
    }

    public boolean is(String other) {
        return contains(other);
    }

    @Override
    public String getValueString() {
        return String.join(", ", values);
    }

    @Override
    public boolean setValueString(String string) {
        if (string == null) return false;
        values.clear();
        for (String part : string.split(",")) add(part);
        return true;
    }
}
