package cc.squall.client.module.Setting;

import cc.squall.client.module.Module;
import lombok.Getter;

public abstract class Setting {
    @Getter
    final String name;
    @Getter
    Module owner;
    public Setting(String namex,Module ownerx ) {
        this(namex, ownerx, true);
    }

    /** register=false skips adding to the owner's setting list, for settings that are only ever accessed through a wrapper. */
    protected Setting(String namex, Module ownerx, boolean register) {
        name = namex;
        owner = ownerx;
        if (register) ownerx.getSettings().add(this);
    }

    public abstract String getValueString();

    /** Returns false if the text isn't a valid value for this setting. */
    public abstract boolean setValueString(String value);

}
