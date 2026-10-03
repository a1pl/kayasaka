package cc.squall.client.events.Update;

import cc.squall.client.events.CustomEvent;

// posted once per frame from Display.update, so it is not gated on being in a world
// or on the 20hz tick loop
public class EventRenderUpdate extends CustomEvent {
    public EventRenderUpdate() {}

}
