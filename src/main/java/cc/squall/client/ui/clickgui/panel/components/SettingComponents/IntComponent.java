package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.module.Setting.impl.SliderSetting;

public class IntComponent extends LimitedEditableComponent {
    public IntComponent(SliderSetting setting) {
        super(setting, setting.getMin(), setting.getMax());
    }

    @Override
    protected boolean accepts(char c) {
        return (c >= '0' && c <= '9') || c == '-' || c == '.';
    }
}
