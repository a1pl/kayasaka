package cc.squall.client.module.Setting.impl;

import cc.squall.client.module.Module;
import cc.squall.client.module.Setting.Displayable;
import cc.squall.client.module.Setting.Setting;
import lombok.Getter;

import java.util.Arrays;

// modesetting1 wrapper
public class EnumSetting<E extends Enum<E>> extends Setting {
    @Getter
    private final Class<E> type;
    @Getter
    private final ModeSetting mode;

    public EnumSetting(String namex, Module ownerx, E value) {
        super(namex, ownerx);
        this.type = value.getDeclaringClass();
        String[] options = Arrays.stream(type.getEnumConstants())
                .map(EnumSetting::display)
                .toArray(String[]::new);
        this.mode = new ModeSetting(namex, ownerx, false, display(value), options);
    }

    public E[] getOptions() {
        return type.getEnumConstants();
    }

    public E getValue() {
        for (E option : getOptions()) {
            if (display(option).equalsIgnoreCase(mode.getValue())) return option;
        }
        return getOptions()[0];
    }
    public void setValue(E value) {
        if (value != null) {
            mode.setValueString(display(value));
        }
    }

    public boolean is(String other) {
        return mode.is(other);
    }

    public static String display(Enum<?> option) {
        return option instanceof Displayable ? ((Displayable) option).getDisplayName() : option.name();
    }

    @Override
    public String getValueString() {
        return mode.getValueString();
    }

    @Override
    public boolean setValueString(String string) {
        return mode.setValueString(string);
    }
}
