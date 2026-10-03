package cc.squall.client.ui.clickgui.panel.components;

import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.PaintMode;
import io.github.humbleui.types.Rect;

public class SliderKnob extends Component {
    float border = 1;

    public SliderKnob() {
        super("knob");
        this.width = 6;
        this.height = 6;
    }

    @Override
    protected void drawSelf(Canvas c) {
        try (Paint paint = new Paint().setColor(ColorUtil.active)) {
            c.drawRect(Rect.makeXYWH(xpos, ypos, width, height), paint);
            paint.setColor(ColorUtil.panel).setMode(PaintMode.STROKE).setStrokeWidth(border);
            c.drawRect(Rect.makeXYWH(xpos + border / 2, ypos + border / 2, width - border, height - border), paint);
        }
    }
}
