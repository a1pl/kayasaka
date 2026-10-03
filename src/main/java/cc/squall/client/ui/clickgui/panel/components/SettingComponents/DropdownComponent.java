package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.module.Setting.Setting;
import cc.squall.client.ui.clickgui.panel.components.Component;
import cc.squall.client.utils.render.fonts.FontUtil;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import lombok.Getter;
import lombok.Setter;

public class DropdownComponent extends Component {

    // float or expand
    public enum Mode { Overlay, Expand }

    @Getter
    private final Setting setting;
    @Getter @Setter
    private Mode mode = Mode.Expand;

    public DropdownComponent(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.height = 12;
    }
    protected String valueText() {
        return setting.getValueString();
    }

    @Override
    protected boolean childrenTakeSpace() {
        return mode == Mode.Expand;
    }

    @Override
    protected boolean drawsAsOverlay() {
        return mode == Mode.Overlay && expanded;
    }

    @Override
    public void mouseClicked(float x, float y, int button) {
        if (contains(x, y) && (button == 0 || button == 1)) {
            toggleExpanded();
            return;
        }
        super.mouseClicked(x, y, button);
    }

    @Override
    protected void drawSelf(Canvas c) {
        super.drawSelf(c);

        Font font = FontUtil.skija("sf", fontSize);
        String shown = name + ": " + valueText();
        int split = name.length() + 2;
        float tx = xpos + pad + textOffset - FontUtil.inkLeft(font, shown);
        FontUtil.drawSkijaRangeCentered(c, font, shown, 0, split, tx, ypos, height, FontUtil.Band.capital, ColorUtil.textLabel);
        FontUtil.drawSkijaRangeCentered(c, font, shown, split, shown.length(), tx, ypos, height, FontUtil.Band.capital, ColorUtil.text);
    }
}
