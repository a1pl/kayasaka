package cc.squall.client.events.netowrking;

import cc.squall.client.events.CustomEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;
@Getter
@Setter
public class EventPacketReceive extends CustomEvent {
    private final Packet<?> packet;

    public EventPacketReceive(Packet<?> packet) {
        try {
            this.packet = packet;
        } catch (ClassCastException e) {
            throw new RuntimeException("Invalid packet received!");
        }
    }
}

