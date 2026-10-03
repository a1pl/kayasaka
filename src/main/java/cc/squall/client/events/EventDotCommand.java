package cc.squall.client.events;

import lombok.Getter;

public class EventDotCommand extends CustomEvent{
    @Getter
    private final String command;
    public EventDotCommand(String commandx) {
        command = commandx;
    }
}
