package cc.squall.client.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class EventRenderItem extends CustomEvent{
    private EnumAction enumAction;
    private boolean useItem;
    private float animationProgression, partialTicks, swingProgress;
    private ItemStack itemToRender;

}
