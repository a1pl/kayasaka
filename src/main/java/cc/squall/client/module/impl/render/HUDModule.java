package cc.squall.client.module.impl.render;

import cc.squall.client.Client;
import cc.squall.client.events.Input.Keyboard.EventKeyPress;
import cc.squall.client.events.Update.EventRender2dUpdate;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleAnnotation;
import cc.squall.client.module.Setting.Displayable;
import cc.squall.client.module.Setting.impl.EnumSetting;
import cc.squall.client.module.Setting.impl.StringSetting;
import cc.squall.client.module.Setting.impl.ToggleSetting;
import cc.squall.client.utils.LWJGL.input.KeyUtil;
import cc.squall.client.utils.datatypes.lists.GlueList;
import cc.squall.client.utils.render.fonts.FontUtil;
import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import cc.squall.client.utils.render.utils.rendering.skia.SkiaGui;
import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.EnumSet;
import java.util.List;

import static cc.squall.client.utils.module.GeneralUtils.getPing;

@ModuleAnnotation
public class HUDModule extends Module {

    private static final float fontSize = 12f;
    private static final float pad = 2f;
    private static final float topPadding = 1f;
    private static final float shadowOffset = 2f;
    private static final float textShift = 2f;
    private static final float bowidth = 1f; //green
    private static final float ar = 1.25f;
    private static final float watermarkScale = 1.75f;
    // niche css reference?
    private static final float ksMargin = 4f;
    private static final float ksFrame = 2.5f;
    private static final float ksPad = 4f;

    private final EnumSetting<FontChoice> fontMode = new EnumSetting<>("Font", this, FontChoice.Sf);
    private final GlueList<Category> categories = new GlueList<>(EnumSet.allOf(Category.class));

    private String loadedFont;
    private Font font;
    private Font watermarkFont;
    private float rowHeight;
    private final EnumSetting<Watermark> clientMode = new EnumSetting<>("Watermark", this, Watermark.Default);
    private final StringSetting clientName = new StringSetting("Watermark Name", this, "");
    private final ToggleSetting modulesToggle= new ToggleSetting("TabGUI", this, true);
    private final ToggleSetting arraylist= new ToggleSetting("ArrayList", this, true);

    private float boxX, boxY, boxWidth, boxHeight, moduleBoxX, watermarkHeight;
    private float catBarY, modBarY; // animated y of the selection bars
    private final float[] categoryShift = new float[Category.values().length]; // animated x offset of each row's text
    private float[] moduleShift = new float[0];
    private int currentCategoryInt = 0;
    private int currentModuleInt = 0;

    private enum Control { MODULES, CATEGORIES }
    private Control control = Control.CATEGORIES;

    public HUDModule() {
        super("HUD", "its a hud gng", Category.Render, false);
    }

    private void ensureInit() {
        if (fontMode.getValueString().equals(loadedFont)) return;
        loadedFont = fontMode.getValueString();

        font = FontUtil.skija(loadedFont, fontSize);
        watermarkFont = FontUtil.skija(loadedFont, fontSize * watermarkScale);

        FontMetrics m = font.getMetrics();
        rowHeight = m.getDescent() - m.getAscent();

        FontMetrics wm = watermarkFont.getMetrics();
        watermarkHeight = wm.getDescent() - wm.getAscent();

        boxHeight = topPadding + categories.size() * rowSlot();
        boxWidth = 0;
        for (Category c : categories) {
            boxWidth = Math.max(boxWidth, FontUtil.inkWidth(font, c.name()));
        }
        boxWidth = windowWidth(boxWidth, boxHeight);
    }

    private float rowSlot() {
        return (float) Math.ceil(rowHeight + pad);
    }

    private float windowWidth(float textWidth, float height) {
        return (float) Math.ceil(Math.max(textWidth + pad * 2 + textShift, height * ar));
    }

    private float kayasenseHeight() {
        return rowHeight + 2 * (ksPad + ksFrame);
    }

    private void updateLayout() {
        if (clientMode.is("kayasense")) {
            boxX = ksMargin;
            boxY = Math.round(ksMargin + kayasenseHeight() + pad);
        } else {
            boxX = 1;
            boxY = Math.round(1 + watermarkHeight + pad);
        }
        moduleBoxX = boxX + boxWidth + pad;
    }

    @Override
    public void onRender2dUpdate(EventRender2dUpdate event) {
        ensureInit();
        updateLayout();

        SkiaGui.draw(event.getSr(), canvas -> {

            if (arraylist.isToggled()) {

            }


            // watermark
            String name = clientName.getValueString();

            if (clientMode.is("Default")) {
                name = Client.getInstance().getClientName();
                if (!name.isEmpty()) {
                    FontUtil.drawSkija(canvas, watermarkFont, name, 1 + shadowOffset, 1 + shadowOffset, ColorUtil.shadow);
                    FontUtil.drawSkijaRange(canvas, watermarkFont, name, 0, 1, 1, 1, ColorUtil.active);
                    FontUtil.drawSkijaRange(canvas, watermarkFont, name, 1, name.length(), 1, 1, ColorUtil.text);
                }
            } else if (clientMode.is("kayasense")) {
                float x = ksMargin;
                float y = ksMargin;

                String server = Client.getInstance().getMc().isSingleplayer()
                        ? "singleplayer"
                        : (Client.getInstance().getMc().getCurrentServerData() != null
                        ? Client.getInstance().getMc().getCurrentServerData().serverIP
                        : "unknown");
                String label = "kayasense - " + Client.getInstance().getMc().thePlayer.getName() + " - " + server + " - " + getPing();
                float W = FontUtil.width(font, label);

                float panelW = W + 2 * ksPad;
                float panelH = rowHeight + 2 * ksPad;
                float outerW = panelW + 2 * ksFrame;
                float outerH = panelH + 2 * ksFrame;

                rect(canvas, x, y, outerW, outerH, ColorUtil.frame);
                drawBorderedBox(canvas, x + 1f, y + 1f, outerW - 2f, outerH - 2f, ColorUtil.frame, ColorUtil.frameLine, 0.5f);
                rect(canvas, x + ksFrame, y + ksFrame, panelW, panelH, ColorUtil.panel);
                rect(canvas, x + ksFrame, y + ksFrame + panelH - 1f, panelW, 1f, ColorUtil.active);
                rect(canvas, x + ksFrame, y + ksFrame + panelH + 0.5f, panelW, 0.5f, ColorUtil.frameLine);

                float tx = x + ksFrame + ksPad;
                float ty = y + ksFrame + ksPad;

                FontUtil.drawSkijaRangeCentered(canvas, font, label, 0, 4, tx, ty, rowHeight, FontUtil.Band.X, ColorUtil.text);
                FontUtil.drawSkijaRangeCentered(canvas, font, label, 4, 9, tx, ty, rowHeight, FontUtil.Band.X, ColorUtil.active);
                FontUtil.drawSkijaRangeCentered(canvas, font, label, 9, label.length(), tx, ty, rowHeight, FontUtil.Band.X, ColorUtil.text);
            }
            if (modulesToggle.isToggled()) {
                // categories
                rect(canvas, boxX, boxY, boxWidth, boxHeight, ColorUtil.background);
                catBarY += (boxY + topPadding + currentCategoryInt * rowSlot() - catBarY) * 0.2f;
                drawSelection(canvas, boxX, catBarY, boxWidth, boxY + topPadding, boxHeight - topPadding);
                for (int i = 0; i < categories.size(); i++) {
                    String label = categories.get(i).name();
                    categoryShift[i] += ((i == currentCategoryInt ? textShift : 0) - categoryShift[i]) * 0.2f;
                    FontUtil.drawSkija(canvas, font, label, boxX + pad + categoryShift[i] - FontUtil.inkLeft(font, label), boxY + topPadding + i * rowSlot() + pad / 2,
                            i == currentCategoryInt ? ColorUtil.text : ColorUtil.textInactive);
                }
                border(canvas, boxX, boxY, boxWidth, boxHeight);

                if (control == Control.MODULES) {
                    drawModuleWindow(canvas, categories.get(currentCategoryInt), boxY + topPadding + currentCategoryInt * rowSlot());
                }
            }
        });
    }

    private void drawArrayList(Canvas canvas) {
        List<Module> modules = Client.getInstance().getModman().getModules();
        
    }

    private void drawModuleWindow(Canvas canvas, Category category, float top) {
        List<Module> modules = Client.getInstance().getModman().getModules(category);

        float height = modules.size() * rowSlot();
        float width = 0;
        for (Module module : modules) {
            width = Math.max(width, FontUtil.inkWidth(font, module.getName()));
        }
        width = windowWidth(width, height);

        rect(canvas, moduleBoxX, top, width, height, ColorUtil.background);
        modBarY += (top + currentModuleInt * rowSlot() - modBarY) * 0.2f;
        drawSelection(canvas, moduleBoxX, modBarY, width, top, height);
        if (moduleShift.length != modules.size()) moduleShift = new float[modules.size()]; // a different category has a different row count
        for (int i = 0; i < modules.size(); i++) {
            String name = modules.get(i).getName();
            moduleShift[i] += ((i == currentModuleInt ? textShift : 0) - moduleShift[i]) * 0.2f;
            // module text inactive till enabled
            int rowColor = modules.get(i).isEnabled() ? ColorUtil.text
                    : (i == currentModuleInt ? ColorUtil.textSelected : ColorUtil.textInactive);
            FontUtil.drawSkija(canvas, font, name, moduleBoxX + pad + moduleShift[i] - FontUtil.inkLeft(font, name), top + i * rowSlot() + pad / 2,
                    rowColor);
        }
        border(canvas, moduleBoxX, top, width, height);
    }



    // clamp fix
    private void drawSelection(Canvas canvas, float x, float y, float width, float top, float height) {
        rect(canvas, x, Math.max(top, Math.min(y, top + height - rowSlot())), width, rowSlot(), ColorUtil.selection);
    }

    private void border(Canvas canvas, float x, float y, float w, float h) {
        try (Paint p = new Paint().setColor(ColorUtil.active).setMode(PaintMode.STROKE).setStrokeWidth(bowidth)) {
            canvas.drawRect(Rect.makeXYWH(x + bowidth / 2, y + bowidth / 2, w - bowidth, h - bowidth), p);
        }
    }

    private void rect(Canvas canvas, float x, float y, float w, float h, int argb) {
        try (Paint paint = new Paint().setColor(argb)) {
            canvas.drawRect(Rect.makeXYWH(x, y, w, h), paint);
        }
    }
    @Override
    public void onKeyPress(EventKeyPress event) {
        ensureInit();
        String keyString = KeyUtil.findKeyString(event.getKeyCode());
        if (keyString == null) return;

        switch (keyString) {
            case "KEY_UP":
                if (control == Control.CATEGORIES) currentCategoryInt--;
                else currentModuleInt--;
                break;
            case "KEY_DOWN":
                if (control == Control.CATEGORIES) currentCategoryInt++;
                else currentModuleInt++;
                break;
            case "KEY_RIGHT": {
                List<Module> modules = Client.getInstance().getModman().getModules(categories.get(currentCategoryInt));
                if (control == Control.CATEGORIES) {
                    if (!modules.isEmpty()) {
                        control = Control.MODULES;
                        currentModuleInt = 0;
                    }
                } else {
                    modules.get(currentModuleInt).toggle();
                }
                break;
            }
            case "KEY_LEFT":
                if (control == Control.MODULES) control = Control.CATEGORIES;
                break;
        }

        if (!categories.isEmpty()) {
            currentCategoryInt = Math.floorMod(currentCategoryInt, categories.size());
        }

        List<Module> currentModules = categories.isEmpty() ? null
                : Client.getInstance().getModman().getModules(categories.get(currentCategoryInt));
        if (currentModules != null && !currentModules.isEmpty()) {
            currentModuleInt = Math.floorMod(currentModuleInt, currentModules.size());
        } else {
            currentModuleInt = 0;
        }

    }
    void drawKayasenseText(Canvas canvas, Font font, String a, String b, float x, float y, int colA, int colB) {
        float baseline = y - font.getMetrics().getAscent();
        try (Paint p = new Paint().setColor(colA)) {
            canvas.drawString(a, x, baseline, font, p);
            p.setColor(colB);
            canvas.drawString(b, x + font.measureTextWidth(a), baseline, font, p);
        }
    }
    void drawBorderedBox(Canvas canvas, float x, float y, float w, float h, int fill, int border, float bw) {
        try (Paint p = new Paint().setColor(fill)) {
            canvas.drawRect(Rect.makeXYWH(x, y, w, h), p);
            p.setColor(border).setMode(PaintMode.STROKE).setStrokeWidth(bw);
            canvas.drawRect(Rect.makeXYWH(x + bw / 2, y + bw / 2, w - bw, h - bw), p);
        }
    }

    public enum FontChoice implements Displayable {
        Sf("sf"),
        SfBold("sf-bold"),
        Tahoma("tahoma"),
        TahomaBold("tahoma-bold");

        private final String displayName;

        FontChoice(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Watermark implements Displayable {
        Default("Default"),
        Text("String"),
        Kayasense("kayasense");

        private final String displayName;

        Watermark(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }
    }
}
