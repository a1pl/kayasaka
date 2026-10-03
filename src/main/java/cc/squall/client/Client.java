package cc.squall.client;

import cc.squall.client.events.MethodEventShim;
import cc.squall.client.utils.module.rots.RotationManager;
import cc.squall.client.ui.clickgui.ClickGUIManager;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleManager;
import cc.squall.client.module.impl.movement.Sprint;
import cc.squall.client.module.impl.render.Animations;
import cc.squall.client.module.impl.render.HUDModule;
import cc.squall.client.utils.render.utils.cosmetics.obj.objloader;
import de.florianmichael.viamcp.ViaMCP;
import dev.tigr.simpleevents.event.Event;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import org.lwjglx.opengl.Display;

public class Client {

    @Getter
    private final boolean dev = true; // change for builds
    @Getter
    private static Client instance;



    @Getter
    private final Minecraft mc = Minecraft.getMinecraft();
    @Getter
    private final FontRenderer fr = mc.fontRendererObj;
    @Getter
    private final ModuleManager modman = new ModuleManager();
    @Getter
    private final MethodEventShim shim = new MethodEventShim();
    @Getter
    private final ClickGUIManager clickGuiManager = new ClickGUIManager();
    @Getter
    private final RotationManager rotman = new RotationManager();
    @Getter
    private objloader objLoader;
    public Client() {
        instance = this;
    }
    @Getter
    String clientName;

    public void init() {
        try {
            ViaMCP.create();
        } catch (Exception e) {
            e.printStackTrace();
        }

        modman.init();
        clickGuiManager.init();
        rotman.init();

        if (!dev) {
            clientName = "Kayasaka";
        } else {
            clientName = "Kayasaka Dev";
            modman.toggle(modman.getByClass().get(Sprint.class));
            modman.toggle(modman.getByClass().get(HUDModule.class));
            modman.toggle(modman.getByClass().get(Animations.class));

        }
        Display.setTitle(clientName);
        GuiMainMenu menu = (GuiMainMenu) mc.getCurrentScreen();
        if (menu != null) {
            menu.setS(clientName);
        }
        objLoader = new objloader();
    }

    // ts pmo
    public static boolean isClickGuiOpen() {
        return instance != null && instance.clickGuiManager.isAnyShown();
    }

    public static void post(Event event) {
        if (instance == null) return;
        instance.shim.post(event);
    }
}