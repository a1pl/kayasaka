package cc.squall.client.events.Update;

import cc.squall.client.events.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.ScaledResolution;

@AllArgsConstructor
public class EventRender2dUpdate extends CustomEvent {
    @Getter
    public final float partialTicks;
    @Getter
    public final ScaledResolution sr;
}
