package cc.squall.client.ui.clickgui.panel.components;

import cc.squall.client.utils.render.fonts.FontUtil;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;

public class Header extends Component {
    float fontSize = 11;
    float line = 1;

    public Header(String name) {
        super(name);
        this.height = 14;
    }

    @Override
    protected void drawSelf(Canvas c) {
        try (Paint paint = new Paint().setColor(ColorUtil.background)) {
            c.drawRect(Rect.makeXYWH(xpos, ypos, width, height), paint);
        }

        Font font = FontUtil.skija("sf", fontSize);
        FontUtil.drawSkijaCentered(c, font, name, xpos + pad - FontUtil.inkLeft(font, name),
                ypos, height - line, FontUtil.Band.capital, ColorUtil.text);

        try (Paint paint = new Paint().setColor(ColorUtil.active)) {
            c.drawRect(Rect.makeXYWH(xpos, ypos + height - line, width, line), paint);
        }
    }
}
