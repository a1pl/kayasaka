package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.module.Setting.Setting;
import lombok.Getter;

public abstract class LimitedEditableComponent extends EditableComponent {
    @Getter
    private final double min, max;

    public LimitedEditableComponent(Setting setting, double min, double max) {
        super(setting);
        this.min = min;
        this.max = max;
    }
}
