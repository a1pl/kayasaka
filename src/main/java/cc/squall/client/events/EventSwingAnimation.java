package cc.squall.client.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
public final class EventSwingAnimation extends CustomEvent {
    @Getter
    @Setter
    private int animationEnd;

}