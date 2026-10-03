package cc.squall.client.events.pLaYeR;

import cc.squall.client.events.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.lwjglx.util.vector.Vector2f;
//yuri
@Getter
@Setter
@AllArgsConstructor
public class EventPlayerLook extends CustomEvent {
    private Vector2f rotation;
}
