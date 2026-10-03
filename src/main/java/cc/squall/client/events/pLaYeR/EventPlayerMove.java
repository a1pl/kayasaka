package cc.squall.client.events.pLaYeR;

import cc.squall.client.events.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
//yuri
@Getter
@Setter
@AllArgsConstructor
public class EventPlayerMove extends CustomEvent {
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlowDownMultiplier;
}