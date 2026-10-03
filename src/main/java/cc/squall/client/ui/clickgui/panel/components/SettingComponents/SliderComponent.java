package cc.squall.client.ui.clickgui.panel.components.SettingComponents;

import cc.squall.client.module.Setting.impl.SliderSetting;
import cc.squall.client.ui.clickgui.panel.components.Component;
import cc.squall.client.ui.clickgui.panel.components.SliderKnob;
import cc.squall.client.utils.render.fonts.FontUtil;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import lombok.Getter;

public class SliderComponent extends Component {
    @Getter
    private final SliderSetting setting;
    private final SliderKnob knob = new SliderKnob();

    private boolean dragging;

    float textHeight = 11;
    float track = 2;

    public SliderComponent(SliderSetting setting) {
        super(setting.getName());
        this.setting = setting;
        this.height = 17;
    }

    private float trackLeft() {
        return xpos + pad + textOffset;
    }

    private float trackWidth() {
        return Math.max(1f, width - pad * 2 - textOffset);
    }

    private float trackY() {
        return ypos + textHeight + (height - textHeight - track) / 2;
    }

    private float fraction() {
        double span = setting.getMax() - setting.getMin();
        if (span <= 0) return 0;
        return (float) Math.max(0, Math.min(1, (setting.getValue() - setting.getMin()) / span));
    }

    private void setFromX(float x) {
        float usable = trackWidth() - knob.getWidth();
        if (usable <= 0) return;
        float f = Math.max(0, Math.min(1, (x - trackLeft() - knob.getWidth() / 2f) / usable));
        setting.setValue(setting.getMin() + f * (setting.getMax() - setting.getMin()));
    }

    @Override
    public void mouseClicked(float x, float y, int button) {
        if (button == 0 && contains(x, y)) {
            dragging = true;
            setFromX(x);
            return;
        }
        super.mouseClicked(x, y, button);
    }

    @Override
    public void mouseReleased(float x, float y, int button) {
        if (button == 0) dragging = false;
        super.mouseReleased(x, y, button);
    }

    @Override
    public void mouseMoved(float x, float y) {
        if (dragging) setFromX(x);
        super.mouseMoved(x, y);
    }

    @Override
    protected void drawSelf(Canvas c) {
        super.drawSelf(c);

        Font font = FontUtil.skija("sf", fontSize);
        String shown = name + ": " + setting.getValueString();
        int split = name.length() + 2;
        float tx = xpos + pad + textOffset - FontUtil.inkLeft(font, shown);
        FontUtil.drawSkijaRangeCentered(c, font, shown, 0, split, tx, ypos, textHeight, FontUtil.Band.capital, ColorUtil.textLabel);
        FontUtil.drawSkijaRangeCentered(c, font, shown, split, shown.length(), tx, ypos, textHeight, FontUtil.Band.capital, ColorUtil.text);

        float ty = trackY();
        float filled = (trackWidth() - knob.getWidth()) * fraction() + knob.getWidth() / 2f;
        try (Paint paint = new Paint().setColor(ColorUtil.frameLine)) {
            c.drawRect(Rect.makeXYWH(trackLeft(), ty, trackWidth(), track), paint);
            paint.setColor(ColorUtil.active);
            c.drawRect(Rect.makeXYWH(trackLeft(), ty, filled, track), paint);
        }

        knob.setXpos(trackLeft() + (trackWidth() - knob.getWidth()) * fraction());
        knob.setYpos(ty + track / 2f - knob.getHeight() / 2f);
        knob.draw(c);
    }
}
