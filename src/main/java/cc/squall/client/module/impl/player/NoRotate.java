package cc.squall.client.module.impl.player;

import cc.squall.client.Client;
import cc.squall.client.events.Update.EventRender2dUpdate;


import cc.squall.client.events.netowrking.EventPacketReceive;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleAnnotation;
import cc.squall.client.module.Setting.impl.EnumSetting;
import cc.squall.client.module.Setting.impl.ModeSetting;
import cc.squall.client.utils.module.GeneralUtils;
import cc.squall.client.utils.module.rots.RotationManager;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleAnnotation
public class NoRotate extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", this, "Silent", "Silent", "Cancel");
    public NoRotate() {
        super("NoRotate", "Prevents server from resetting your view angles", Category.Render, false);
    }
    protected final RotationManager rotman = Client.getInstance().getRotman();
    @Override
    public void onPacketReceive(EventPacketReceive event) {
        if (GeneralUtils.nullCheck() && event.getPacket() instanceof S08PacketPlayerPosLook){
            final S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
            switch (mode.getValueString()) {
                case "Cancel":
                    rotman.setRotationYaw(packet.getYaw());
                    rotman.setRotationPitch(packet.getPitch());
                case "Silent":
                    packet.setYaw(player.rotationYaw);
                    packet.setPitch(player.rotationPitch);
                    break;
            }
        }

    }

}
