package cc.squall.client.ui.clickgui.panel.components;

import cc.squall.client.module.Module;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.ClipMode;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.PaintMode;
import io.github.humbleui.types.Rect;
import lombok.Getter;

public class Panel extends Component{
    @Getter
    Module.Category category;
    float border = 1;

    private final Header header;
    private boolean dragging;
    private float dragX, dragY;

    public Panel(Module.Category cat, float x, float y) {
        super(cat.name());
        this.category = cat;
        this.xpos = x;
        this.ypos = y;
        this.width = 130;
        this.header = new Header(cat.name());
        super.add(this.header);
        layout();
    }

    @Override
    public void add(Component child) {
        super.add(child);
        layout();
    }

    private void layout() {
        float y = ypos;
        for (Component child : children) {
            child.setTextOffset(0);
            y += child.layoutAt(xpos, y, width);
        }
        height = y - ypos;
    }
    
    @Override
    protected boolean showChildren() {
        return true;
    }

    @Override
    protected float childIndent() {
        return 0;
    }

    @Override
    public void mouseClicked(float x, float y, int button) {
        if (button == 0 && header.contains(x, y)) {
            dragging = true;
            dragX = x - xpos;
            dragY = y - ypos;
        }
        super.mouseClicked(x, y, button);
    }

    @Override
    public void mouseReleased(float x, float y, int button) {
        if (button == 0) {
            dragging = false;
        }
        super.mouseReleased(x, y, button);
    }

    @Override
    public void mouseMoved(float x, float y) {
        if (dragging) {
            xpos = x - dragX;
            ypos = y - dragY;
        }
        super.mouseMoved(x, y);
    }

    @Override
    public void draw(Canvas c) {
        layout();
        try (Paint paint = new Paint().setColor(ColorUtil.background)) {
            c.drawRect(Rect.makeXYWH(xpos, ypos, width, height), paint);
            paint.setColor(ColorUtil.active).setMode(PaintMode.STROKE).setStrokeWidth(border);
            c.drawRect(Rect.makeXYWH(xpos + border / 2, ypos + border / 2, width - border, height - border), paint);
        }
        c.save();
        try {
            c.clipRect(Rect.makeXYWH(xpos + border, ypos + border, width - border * 2, height - border * 2), ClipMode.INTERSECT);
            drawChildren(c);
        } finally {
            c.restore();
        }
        // unclipped, so an open dropdown can hang past the panel edge
        drawOverlay(c);
    }
}
