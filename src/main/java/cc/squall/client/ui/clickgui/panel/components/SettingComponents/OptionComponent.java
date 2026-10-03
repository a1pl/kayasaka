package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.ui.clickgui.panel.components.Component;
import cc.squall.client.utils.render.fonts.FontUtil;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;

public class OptionComponent extends Component {

    public interface Handler {
        boolean isSelected(String option);
        void onPick(String option);
    }

    private final Handler handler;


    public OptionComponent(String name, Handler handler) {
        super(name);
        this.handler = handler;
        this.height = 12;
    }

    @Override
    public void mouseClicked(float x, float y, int button) {
        if (button == 0 && contains(x, y)) {
            handler.onPick(name);
            return;
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
                handler.isSelected(name) ? ColorUtil.active : ColorUtil.text);
    }
}
