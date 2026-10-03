package cc.squall.client.module.Setting.impl;

import cc.squall.client.module.Module;
import cc.squall.client.module.Setting.Setting;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// immutable listseteting
public class ModeSetting extends Setting {
    @Getter
    private final List<String> options;
    private String value;

    public ModeSetting(String namex, Module ownerx, String value, String... options) {
        this(namex, ownerx, true, value, options);
    }

    ModeSetting(String namex, Module ownerx, boolean register, String value, String... options) {
        super(namex, ownerx, register);
        this.options = Collections.unmodifiableList(Arrays.asList(options));
        if (!setValueString(value)) this.value = this.options.get(0);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        setValueString(value);
    }

    public boolean is(String other) {
        return value.equalsIgnoreCase(other);
    }

    @Override
    public String getValueString() {
        return value;
    }

    @Override
    public boolean setValueString(String string) {
        if (string == null) return false;
        for (String option : options) {
            if (option.equalsIgnoreCase(string)) {
                value = option;
                return true;
            }
        }
        return false;
    }
}
