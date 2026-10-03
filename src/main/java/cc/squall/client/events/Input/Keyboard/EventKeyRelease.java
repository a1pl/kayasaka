package cc.squall.client.events.Input.Keyboard;

import cc.squall.client.events.CustomEvent;
import cc.squall.client.utils.LWJGL.input.KeyUtil;
import lombok.Getter;

public class EventKeyRelease extends CustomEvent {
    @Getter
    private final int keyCode;
    @Getter
    private final String keyString;

    public EventKeyRelease(int keyCode, String keyString) {
        this.keyCode = keyCode;
        this.keyString = keyString;
    }

    public EventKeyRelease(int keyCode) {
        this.keyCode = keyCode;
        this.keyString = KeyUtil.findKeyString(keyCode);
    }
    public EventKeyRelease(String keyString) {
        this.keyCode = KeyUtil.findKeycode(keyString);
        this.keyString = keyString;
    }
}

