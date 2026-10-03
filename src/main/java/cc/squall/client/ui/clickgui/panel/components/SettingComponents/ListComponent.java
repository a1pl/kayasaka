package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.module.Setting.impl.ListSetting;

public class ListComponent extends MutableDropdownComponent {

    private final ListSetting listSetting;
    private int knownSize = -1;

    public ListComponent(ListSetting setting) {
        super(setting);
        this.listSetting = setting;
        rebuild();
    }

    @Override
    protected String valueText() {
        return listSetting.getValues().size() + " entries";
    }

    @Override
    protected void rebuild() {
        clearOptions();
        for (String value : listSetting.getValues()) {
            // click an entry to remove it, idk mayb add trash bin icon eventually
            addOption(new OptionComponent(value, new OptionComponent.Handler() {
                @Override
                public boolean isSelected(String option) {
                    return true;
                }

                @Override
                public void onPick(String option) {
                    listSetting.remove(option);
                }
            }));
        }
        knownSize = listSetting.getValues().size();
    }

    @Override
    public float layoutAt(float x, float y, float width) {
        if (knownSize != listSetting.getValues().size()) rebuild();
        return super.layoutAt(x, y, width);
    }
}
