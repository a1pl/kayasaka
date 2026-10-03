package cc.squall.client.utils.module;

import cc.squall.client.Client;
import cc.squall.client.utils.IMinecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

public class GeneralUtils implements IMinecraft {
    public static boolean nullCheck() {
        return mc.thePlayer != null && mc.theWorld != null;
    }

    public static int getPing() {
        if (Client.getInstance().getMc().thePlayer == null || Client.getInstance().getMc().getNetHandler() == null)
            return 0;

        NetworkPlayerInfo info =
                Client.getInstance().getMc().getNetHandler().getPlayerInfo(Client.getInstance().getMc().thePlayer.getUniqueID());

        return info != null ? Math.max(info.getResponseTime(), 0) : 0;
    }
}
