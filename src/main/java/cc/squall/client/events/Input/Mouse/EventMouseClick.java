package cc.squall.client.events.Input.Mouse;

import cc.squall.client.events.CustomEvent;
import lombok.Getter;

public class EventMouseClick extends CustomEvent {
    @Getter
    private final int button;
    @Getter
    private final float x, y;

    public EventMouseClick(int button, float x, float y) {
        this.button = button;
        this.x = x;
        this.y = y;
    }
}
