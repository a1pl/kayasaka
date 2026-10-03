package cc.squall.client.events.Input.Mouse;

import cc.squall.client.events.CustomEvent;
import lombok.Getter;

public class EventMouseRightClick extends CustomEvent {
    @Getter
    private final float x, y;

    public EventMouseRightClick(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
