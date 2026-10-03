package cc.squall.client.events;

import dev.tigr.simpleevents.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class MethodEventShim {

    private final Map<Class<?>, List<Consumer<Event>>> listeners = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Event> void subscribe(Class<T> type, Consumer<T> handler) {
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add((Consumer<Event>) handler);
    }

    public void post(Event event) {
        List<Consumer<Event>> handlers = listeners.get(event.getClass());
        if (handlers == null) return;

        for (Consumer<Event> handler : handlers) {
            try {
                handler.accept(event);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
