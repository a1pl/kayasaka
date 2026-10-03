package cc.squall.client.ui.clickgui.panel.components;

import cc.squall.client.module.Module;
import cc.squall.client.utils.render.fonts.FontUtil;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import lombok.Getter;

public class ModuleComponent extends Component {
    @Getter
    private final Module module;

    float fontSize = 11;

    public ModuleComponent(Module module) {
        super(module.getName());
        this.module = module;
        this.height = 12;
    }

    @Override
    public void mouseClicked(float x, float y, int button) {
        if (contains(x, y)) {
            if (button == 0) {
                module.toggle();
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
                ColorUtil.getModuleColor(module.isEnabled()));
    }
}
