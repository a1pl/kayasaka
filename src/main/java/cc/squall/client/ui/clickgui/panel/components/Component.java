package cc.squall.client.ui.clickgui.panel.components;

import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.types.Rect;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {
    @Getter
    protected String name;
    @Getter
    protected final List<Component> children = new ArrayList<>();
    @Getter @Setter
    protected float xpos, ypos, width;
    @Getter @Setter
    protected float height = 12;
    // shared
    @Getter @Setter
    protected float pad = 2;

    @Getter @Setter
    protected float textOffset; // children system
    @Getter
    protected boolean expanded;

    public Component(String name) {
        this.name = name;
    }

    public void add(Component child) {
        children.add(child);
    }
@Getter
        @Setter
    public float fontSize = 8;

    public void toggleExpanded() {
        expanded = !expanded;
    }

    protected boolean showChildren() {
        return expanded;
    }

    protected float childIndent() {
        return children.isEmpty() ? 0 : 2;
    }

    protected boolean childrenTakeSpace() {
        return true;
    }

    protected boolean drawsAsOverlay() {
        return false;
    }

    public float layoutAt(float x, float y, float width) {
        this.xpos = x;
        this.ypos = y;
        this.width = width;
        float used = this.height;
        if (showChildren()) {
            float cy = y + this.height;
            for (Component child : children) {
                child.setTextOffset(this.textOffset + this.childIndent());
                float consumed = child.layoutAt(x, cy, width);
                cy += consumed;
                if (this.childrenTakeSpace()) used += consumed;
            }
        }
        return used;
    }

    public boolean contains(float x, float y) {
        return x >= xpos && x <= xpos + width && y >= ypos && y <= ypos + height;
    }

    public void draw(Canvas c) {
        drawSelf(c);
        drawChildren(c);
    }

    protected void drawSelf(Canvas c) {
        try (Paint paint = new Paint().setColor(ColorUtil.component)) {
            c.drawRect(Rect.makeXYWH(xpos, ypos, width, height), paint);
        }
    }

    public void drawOverlay(Canvas c) {
        if (drawsAsOverlay()) {
            drawSelf(c);
            if (showChildren()) {
                for (Component child : children) child.draw(c);
            }
            return;
        }
        if (!showChildren()) return;
        for (Component child : children) {
            child.drawOverlay(c);
        }
    }

    protected void drawChildren(Canvas c) {
        if (!showChildren()) return;
        for (Component child : children) {
            if (child.drawsAsOverlay()) continue;
            child.draw(c);
        }
    }

    public void mouseClicked(float x, float y, int button) {
        if (!showChildren()) return;
        for (Component child : children) {
            child.mouseClicked(x, y, button);
        }
    }

    public void mouseReleased(float x, float y, int button) {
        if (!showChildren()) return;
        for (Component child : children) {
            child.mouseReleased(x, y, button);
        }
    }

    public void mouseMoved(float x, float y) {
        if (!showChildren()) return;
        for (Component child : children) {
            child.mouseMoved(x, y);
        }
    }
}
