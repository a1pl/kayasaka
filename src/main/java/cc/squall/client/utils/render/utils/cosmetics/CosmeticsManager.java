package cc.squall.client.utils.render.utils.cosmetics;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CosmeticsManager {
    // example cosmetic is at https://github.com/CuteNyami/MCP-Snippets/tree/main/OBJ-Cosmetics/minecraft/assets/client/cosmetics

    public static List<CosmeticsHandler> cosmetics = new ArrayList<>();

    public static void registerCosmetics(CosmeticsHandler... handlers) {
        cosmetics.addAll(Arrays.asList(handlers));
    }

    public static ResourceLocation getWingTexture() {
        return null;
    }
}