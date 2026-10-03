package cc.squall.client.events.Input.Mouse;

import cc.squall.client.events.CustomEvent;
import lombok.Getter;

public class EventMouseMove extends CustomEvent {
    @Getter
    private final float prevX, prevY, newX, newY;

    public EventMouseMove(float prevX, float prevY, float newX, float newY) {
        this.prevX = prevX;
        this.prevY = prevY;
        this.newX = newX;
        this.newY = newY;
    }
}
