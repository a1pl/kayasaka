package cc.squall.client.events.Input.Mouse;

import cc.squall.client.events.CustomEvent;
import lombok.Getter;

public class EventMouseRelease extends CustomEvent {
    @Getter
    private final int button;
    @Getter
    private final float x, y;
    @Getter
    private final double heldMillis;

    public EventMouseRelease(int button, float x, float y, double heldMillis) {
        this.button = button;
        this.x = x;
        this.y = y;
        this.heldMillis = heldMillis;
    }
}
