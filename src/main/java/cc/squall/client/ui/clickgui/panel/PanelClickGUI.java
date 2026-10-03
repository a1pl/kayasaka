package cc.squall.client.ui.clickgui.panel;

import cc.squall.client.events.Update.EventRender2dUpdate;
import cc.squall.client.module.Module;
import cc.squall.client.events.Input.Mouse.EventMouseClick;
import cc.squall.client.events.Input.Mouse.EventMouseMove;
import cc.squall.client.events.Input.Mouse.EventMouseRelease;
import cc.squall.client.ui.clickgui.ClickGUI;
import cc.squall.client.ui.clickgui.panel.components.Component;
import cc.squall.client.ui.clickgui.panel.components.Panel;
import cc.squall.client.utils.render.utils.rendering.skia.SkiaGui;

import lombok.Getter;

import java.util.ArrayList;

public class PanelClickGUI extends ClickGUI {
    @Getter
    ArrayList<Panel> panels = new ArrayList<Panel>();

    public PanelClickGUI() {
        super("Panel");
        float x = 4;
        for (Module.Category category : Module.Category.values()) {
            Panel panel = new Panel(category, x, 4);
            panels.add(panel);
            x += panel.getWidth() + 4;
        }
    }

    @Override
    public void onRender2d(EventRender2dUpdate event) {
        SkiaGui.draw(event.getSr(), canvas -> {
            for (Panel panel : panels) {
                panel.draw(canvas);
            }
        });
    }

    @Override
    public void onMouseClick(EventMouseClick event) {
        for (Panel panel : panels) {
            panel.mouseClicked(event.getX(), event.getY(), event.getButton());
        }
    }

    @Override
    public void onMouseRelease(EventMouseRelease event) {
        for (Panel panel : panels) {
            panel.mouseReleased(event.getX(), event.getY(), event.getButton());
        }
    }

    @Override
    public void onMouseMove(EventMouseMove event) {
        for (Panel panel : panels) {
            panel.mouseMoved(event.getNewX(), event.getNewY());
        }
    }

    public Panel getPanel(Module.Category category) {
        for (Panel panel : panels) {
            if (panel.getName().equals(category.name())) {
                return panel;
            }
        }
        return null;
    }

    public void addToAll(java.util.function.Function<Module.Category, Component> factory) {
        for (Panel panel : panels) {
            panel.add(factory.apply(Module.Category.valueOf(panel.getName())));
        }
    }
}
