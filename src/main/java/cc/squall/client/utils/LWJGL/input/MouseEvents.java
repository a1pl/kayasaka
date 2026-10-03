package cc.squall.client.utils.LWJGL.input;

import cc.squall.client.Client;
import cc.squall.client.events.Input.Mouse.EventMouseClick;
import cc.squall.client.events.Input.Mouse.EventMouseLeftClick;
import cc.squall.client.events.Input.Mouse.EventMouseLeftRelease;
import cc.squall.client.events.Input.Mouse.EventMouseMove;
import cc.squall.client.events.Input.Mouse.EventMouseRelease;
import cc.squall.client.events.Input.Mouse.EventMouseRightClick;
import cc.squall.client.events.Input.Mouse.EventMouseRightRelease;

//glfw callback no events
public class MouseEvents {

    private static final long[] pressTime = new long[16];
    private static float lastX, lastY;

    public static void button(int button, boolean pressed) {
        if (Client.getInstance() == null || button < 0 || button >= pressTime.length) return;
        float x = MouseUtil.getX();
        float y = MouseUtil.getY();

        if (pressed) {
            pressTime[button] = System.currentTimeMillis();
            Client.post(new EventMouseClick(button, x, y));
            if (button == 0) Client.post(new EventMouseLeftClick(x, y));
            else if (button == 1) Client.post(new EventMouseRightClick(x, y));
        } else {
            double held = System.currentTimeMillis() - pressTime[button];
            Client.post(new EventMouseRelease(button, x, y, held));
            if (button == 0) Client.post(new EventMouseLeftRelease(x, y, held));
            else if (button == 1) Client.post(new EventMouseRightRelease(x, y, held));
        }
    }

    public static void moved() {
        if (Client.getInstance() == null) return;
        float x = MouseUtil.getX();
        float y = MouseUtil.getY();
        if (x == lastX && y == lastY) return;
        Client.post(new EventMouseMove(lastX, lastY, x, y));
        lastX = x;
        lastY = y;
    }
}
