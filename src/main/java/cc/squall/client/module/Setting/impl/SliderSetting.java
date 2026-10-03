package cc.squall.client.module.Setting.impl;

import cc.squall.client.module.Module;
import cc.squall.client.module.Setting.Setting;
import lombok.Getter;

import java.math.BigDecimal;

//slidere setting, no dual
public class SliderSetting extends Setting {

    @Getter private double value;
    @Getter private final double min, max, step;

    public SliderSetting(String namex, Module ownerx, double value, double min, double max, double step) {
        super(namex, ownerx);
        this.min = min;
        this.max = max;
        this.step = step <= 0 ? 1 : step;
        this.value = snap(value);
    }

    /** clamp */
    private double snap(double raw) {
        double clamped = Math.max(min, Math.min(max, raw));
        double stepped = min + Math.round((clamped - min) / step) * step;
        return Math.max(min, Math.min(max, Math.round(stepped * 1_000_000d) / 1_000_000d));
    }

    public float getFloat() {
        return (float) value;
    }

    public int getInt() {
        return (int) Math.round(value);
    }

    public void setValue(double newValue) {
        value = snap(newValue);
    }

    public void increment() {
        setValue(value + step);
    }

    public void decrement() {
        setValue(value - step);
    }

    @Override
    public String getValueString() {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    @Override
    public boolean setValueString(String string) {
        if (string == null) return false;
        double parsed;
        try {
            parsed = Double.parseDouble(string.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        if (Double.isNaN(parsed) || Double.isInfinite(parsed) || parsed < min || parsed > max) return false;
        setValue(parsed);
        return true;
    }

    public boolean is(String other) {
        return getValueString().equalsIgnoreCase(other);
    }
}
