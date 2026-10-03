package cc.squall.client.events;

import dev.tigr.simpleevents.event.Event;

public class CustomEvent extends Event {
    public void setCancelled() {
        setCancelled(true);// animations skid helper
    }
}
