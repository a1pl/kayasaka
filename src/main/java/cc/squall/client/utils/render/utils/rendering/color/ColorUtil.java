package cc.squall.client.utils.render.utils.rendering.color;

public class ColorUtil {
    public static final int active = ColorUtils.fromHex("#A7C080").getRGB();
    public static final int inactive = ColorUtils.fromHex("#64734D").getRGB();

    // text
    public static final int text = ColorUtils.fromHex("#FFFFFF").getRGB();
    public static final int textInactive = ColorUtils.fromHex("#534D4B").getRGB();
    public static final int textSelected = ColorUtils.fromHex("#F2F2F2").getRGB();
    public static final int textLabel = ColorUtils.fromHex("#C8C8C8").getRGB();

    // hud windows
    public static final int background = ColorUtils.fromHex("#111213").getRGB();
    public static final int selection = active; // the bar behind the selected row

    // watermark box
    public static final int frame = ColorUtils.fromHex("#3B3939").getRGB();
    public static final int frameLine = ColorUtils.fromHex("#292727").getRGB();
    public static final int panel = ColorUtils.fromHex("#171717").getRGB();

    // clickgui
    public static final int component = ColorUtils.fromHex("#2B2B2B").getRGB();

    public static final int shadow = ColorUtils.setAlpha(ColorUtils.fromHex("#000000"), 0.6).getRGB();

    public static int getModuleColor(boolean enabled) {
        return enabled ? active : inactive;
    }
}
