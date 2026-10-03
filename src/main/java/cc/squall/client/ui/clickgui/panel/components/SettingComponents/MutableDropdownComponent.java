package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.module.Setting.Setting;
import cc.squall.client.ui.clickgui.panel.components.Component;


// only exists for readability
public abstract class MutableDropdownComponent extends DropdownComponent {

    public MutableDropdownComponent(Setting setting) {
        super(setting);
    }

    protected abstract void rebuild();

    protected void clearOptions() {
        children.clear();
    }

    protected void addOption(Component option) {
        add(option);
    }
}
