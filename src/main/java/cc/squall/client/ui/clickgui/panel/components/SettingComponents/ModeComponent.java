package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.module.Setting.impl.ModeSetting;

public class ModeComponent extends DropdownComponent {

    private final ModeSetting modeSetting;

    public ModeComponent(ModeSetting setting) {
        super(setting);
        this.modeSetting = setting;
        for (String option : setting.getOptions()) {
            add(new OptionComponent(option, new OptionComponent.Handler() {
                @Override
                public boolean isSelected(String option) {
                    return modeSetting.getValueString().equalsIgnoreCase(option);
                }

                @Override
                public void onPick(String option) {
                    modeSetting.setValueString(option);
                }
            }));
        }
    }
}
