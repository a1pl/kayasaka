package cc.squall.client.events.netowrking;

import cc.squall.client.events.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@Setter
@AllArgsConstructor
public class EventPacketSend extends CustomEvent {
    private Packet<?> packet;

}
