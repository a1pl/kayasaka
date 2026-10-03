package cc.squall.client.events.Update;

import cc.squall.client.events.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class EventRender3dUpdate extends CustomEvent {
    private final float partialTicks;
}
