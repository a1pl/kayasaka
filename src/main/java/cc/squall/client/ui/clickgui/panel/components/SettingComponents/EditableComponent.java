package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.module.Setting.Setting;
import cc.squall.client.ui.clickgui.panel.components.Component;
import cc.squall.client.utils.render.fonts.FontUtil;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import lombok.Getter;
import lombok.Setter;

public abstract class EditableComponent extends Component {
    @Getter
    private final Setting setting;
    @Setter
    @Getter
    String value;
    @Getter
    private boolean focused;

    public EditableComponent(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.value = setting.getValueString();
        this.height = 12;
    }

    protected boolean accepts(char c) {
        return c >= ' ';
    }

    public void type(char c) {
        if (!focused) return;
        if (c == '\b') {
            if (!value.isEmpty()) value = value.substring(0, value.length() - 1);
        } else if (c == '\n' || c == '\r') {
            commit();
        } else if (accepts(c)) {
            value = value + c;
        }
    }


    public void commit() {
        if (!setting.setValueString(value)) {
            value = setting.getValueString();
        }
        focused = false;
    }

    @Override
    public void mouseClicked(float x, float y, int button) {
        if (button == 0) {
            boolean hit = contains(x, y);
            if (focused && !hit) commit();
            focused = hit;
        } else if (button == 1 && contains(x, y) && !children.isEmpty()) {
            toggleExpanded();
        }
        super.mouseClicked(x, y, button);
    }

    @Override
    protected void drawSelf(Canvas c) {
        super.drawSelf(c);

        Font font = FontUtil.skija("sf", fontSize);
        String shown = name + ": " + (focused ? value + "_" : value);
        int split = name.length() + 2;
        float tx = xpos + pad + textOffset - FontUtil.inkLeft(font, shown);
        FontUtil.drawSkijaRangeCentered(c, font, shown, 0, split, tx, ypos, height, FontUtil.Band.capital, ColorUtil.textLabel);
        FontUtil.drawSkijaRangeCentered(c, font, shown, split, shown.length(), tx, ypos, height, FontUtil.Band.capital, ColorUtil.text);
    }
}
