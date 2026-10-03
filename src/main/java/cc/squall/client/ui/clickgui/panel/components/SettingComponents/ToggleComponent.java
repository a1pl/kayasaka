package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.module.Setting.impl.ToggleSetting;
import cc.squall.client.ui.clickgui.panel.components.Component;
import cc.squall.client.utils.render.fonts.FontUtil;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import lombok.Getter;

public class ToggleComponent extends Component {
    @Getter
    private final ToggleSetting setting;



    public ToggleComponent(ToggleSetting setting) {
        super(setting.getName());
        this.setting = setting;
        this.height = 12;
    }

    @Override
    public void mouseClicked(float x, float y, int button) {
        if (contains(x, y)) {
            if (button == 0) {
                setting.toggle();
            } else if (button == 1) {
                toggleExpanded();
            }
        }
        super.mouseClicked(x, y, button);
    }

    @Override
    protected void drawSelf(Canvas c) {
        super.drawSelf(c);
        Font font = FontUtil.skija("sf", fontSize);
        FontUtil.drawSkijaCentered(c, font, name,
                xpos + pad + textOffset - FontUtil.inkLeft(font, name),
                ypos, height, FontUtil.Band.capital,
                ColorUtil.getModuleColor(setting.getValue()));
    }
}
