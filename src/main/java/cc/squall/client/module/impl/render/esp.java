package cc.squall.client.module.impl.render;

import cc.squall.client.events.Update.EventRender3dUpdate;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleAnnotation;
import cc.squall.client.module.Setting.impl.SliderSetting;
import cc.squall.client.module.Setting.impl.ToggleSetting;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import cc.squall.client.utils.render.utils.rendering.module.EspUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

import java.awt.Color;

@ModuleAnnotation
public class esp extends Module {

    private final ToggleSetting players = new ToggleSetting("Players", this, true);
    private final ToggleSetting mobs = new ToggleSetting("Mobs", this, false);
    private final ToggleSetting items = new ToggleSetting("Items", this, false);
    private final ToggleSetting chests = new ToggleSetting("Chests", this, false);
    private final ToggleSetting self = new ToggleSetting("Self", this, false);
    private final SliderSetting range = new SliderSetting("Range", this, 64, 8, 256, 1);

    // todo: make customizable
    private final Color playerColor = new Color(ColorUtil.active), itemColor = new Color(ColorUtil.active), chestColor = new Color(ColorUtil.active);
    private final Color mobColor = new Color(ColorUtil.textSelected);

    public esp() {
        super("ESP", "See position of entities through walls", Category.Render, false);
        // chams is like yk
    }

    @Override
    public void onRender3dUpdate(EventRender3dUpdate event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        final double max = range.getValue();

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity == null || entity.isDead) continue;
            if (entity == mc.thePlayer && !self.getValue()) continue;
            if (mc.thePlayer.getDistanceToEntity(entity) > max) continue;

            final Color color = colorFor(entity);
            if (color == null) continue;

            EspUtil.entityESPBox(entity, 0, color);
        }

        if (chests.getValue()) {
            for (TileEntity tile : mc.theWorld.loadedTileEntityList) {
                if (!(tile instanceof TileEntityChest) && !(tile instanceof TileEntityEnderChest)) continue;
                if (mc.thePlayer.getDistanceSq(tile.getPos()) > max * max) continue;

                EspUtil.chestESPBox(tile, 0, chestColor);
            }
        }
    }

    // null means not selected category
    private Color colorFor(final Entity entity) {
        if (entity instanceof EntityPlayer) {
            return players.getValue() ? playerColor : null;
        }
        if (entity instanceof EntityItem) {
            return items.getValue() ? itemColor : null;
        }
        if (entity instanceof EntityLivingBase) {
            return mobs.getValue() ? mobColor : null;
        }
        return null;
    }
}
