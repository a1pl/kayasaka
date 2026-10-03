package cc.squall.client.events.Input.Mouse;

import cc.squall.client.events.CustomEvent;
import lombok.Getter;

public class EventMouseLeftRelease extends CustomEvent {
    @Getter
    private final float x, y;
    @Getter
    private final double heldMillis;

    public EventMouseLeftRelease(float x, float y, double heldMillis) {
        this.x = x;
        this.y = y;
        this.heldMillis = heldMillis;
    }
}
