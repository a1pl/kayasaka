package cc.squall.client.utils.render.fonts;

import cc.squall.client.utils.render.utils.rendering.color.ColorUtil;
import cc.squall.client.utils.render.utils.rendering.skia.SkiaGui;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontMetrics;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.TextBlob;
import io.github.humbleui.skija.TextBlobBuilder;
import io.github.humbleui.skija.TextLine;
import io.github.humbleui.types.Rect;
import net.minecraft.client.gui.ScaledResolution;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

// text is shaped once per string and cached. shaping applies the font's kern pairs,
// so substring widths are NOT additive - split spans with drawSkijaRange, never by
// measuring a prefix
public class FontUtil {

    private static final float defaultSize = 18f;
    private static final String fontDir = "/assets/kayasaka/";
    private static final int shapeCacheMax = 512;

    public enum Band { capital, X}

    private static final class Shaped {
        final TextLine line;
        final float inkLeft;
        final float inkWidth;

        Shaped(TextLine line, float inkLeft, float inkWidth) {
            this.line = line;
            this.inkLeft = inkLeft;
            this.inkWidth = inkWidth;
        }
    }

    private static final Map<String, Shaped> shapeCache = new LinkedHashMap<String, Shaped>(64, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Shaped> eldest) {
            if (size() > shapeCacheMax) {
                eldest.getValue().line.close();
                return true;
            }
            return false;
        }
    };

    private static Shaped shape(Font font, String text) {
        return shapeCache.computeIfAbsent(System.identityHashCode(font) + "\u0000" + text, k -> {
            TextLine line = TextLine.make(text, font);
            try (TextBlob blob = line.getTextBlob()) {
                if (blob == null) return new Shaped(line, 0, 0);
                Rect ink = blob.getTightBounds();
                return new Shaped(line, ink.getLeft(), ink.getRight() - ink.getLeft());
            }
        });
    }

    public static float width(Font font, String text) {
        if (text.isEmpty()) return 0;
        return shape(font, text).line.getWidth();
    }

    public static float inkLeft(Font font, String text) {
        if (text.isEmpty()) return 0;
        return shape(font, text).inkLeft;
    }

    public static float inkWidth(Font font, String text) {
        if (text.isEmpty()) return 0;
        return shape(font, text).inkWidth;
    }

    public static float offsetAt(Font font, String text, int charOffset) {
        if (text.isEmpty()) return 0;
        return shape(font, text).line.getCoordAtOffset(charOffset);
    }

    private static float baseline(Font font, float y, float rowHeight, Band band) {
        FontMetrics m = font.getMetrics();
        float bandHeight = band == Band.X ? m.getXHeight() : m.getCapHeight();
        return y + (rowHeight + bandHeight) / 2;
    }

    private static void draw(Canvas canvas, Font font, String text, float x, float baseline, int argb) {
        if (text.isEmpty()) return;
        try (Paint paint = new Paint().setAntiAlias(true).setColor(argb)) {
            canvas.drawTextLine(shape(font, text).line, x, baseline, paint);
        }
    }

    // rebuilds the span out of the already-shaped glyphs. clipping the line instead
    // snaps each edge to a whole pixel and eats a sliver between spans
    private static void drawRange(Canvas canvas, Font font, String text, int from, int to, float x, float baseline, int argb) {
        if (text.isEmpty() || from >= to) return;
        if (from <= 0 && to >= text.length()) {
            draw(canvas, font, text, x, baseline, argb);
            return;
        }

        TextLine line = shape(font, text).line;
        float xFrom = line.getCoordAtOffset(from);
        float xTo = line.getCoordAtOffset(to);
        short[] glyphs = line.getGlyphs();
        float[] positions = line.getPositions();

        short[] runGlyphs = new short[glyphs.length];
        float[] runXs = new float[glyphs.length];
        int count = 0;
        for (int i = 0; i < glyphs.length; i++) {
            float gx = positions[i * 2];
            if (gx >= xFrom - 0.01f && gx < xTo - 0.01f) {
                runGlyphs[count] = glyphs[i];
                runXs[count] = x + gx;
                count++;
            }
        }
        if (count == 0) return;
        if (count < glyphs.length) {
            runGlyphs = Arrays.copyOf(runGlyphs, count);
            runXs = Arrays.copyOf(runXs, count);
        }

        try (TextBlobBuilder builder = new TextBlobBuilder()) {
            builder.appendRunPosH(font, runGlyphs, runXs, baseline);
            try (TextBlob blob = builder.build(); Paint paint = new Paint().setAntiAlias(true).setColor(argb)) {
                if (blob != null) canvas.drawTextBlob(blob, 0, 0, paint);
            }
        }
    }

    public static void drawSkija(Canvas canvas, Font font, String text, float x, float y, int argb) {
        draw(canvas, font, text, x, y - font.getMetrics().getAscent(), argb);
    }

    public static void drawSkijaCentered(Canvas canvas, Font font, String text, float x, float y, float rowHeight, Band band, int argb) {
        draw(canvas, font, text, x, baseline(font, y, rowHeight, band), argb);
    }

    public static void drawSkijaRange(Canvas canvas, Font font, String text, int from, int to, float x, float y, int argb) {
        drawRange(canvas, font, text, from, to, x, y - font.getMetrics().getAscent(), argb);
    }

    public static void drawSkijaRangeCentered(Canvas canvas, Font font, String text, int from, int to, float x, float y, float rowHeight, Band band, int argb) {
        drawRange(canvas, font, text, from, to, x, baseline(font, y, rowHeight, band), argb);
    }

    public static void drawSkija(ScaledResolution sr, Font font, String text, float x, float y, int argb) {
        SkiaGui.draw(sr, canvas -> drawSkija(canvas, font, text, x, y, argb));
    }

    public static void drawSkija(ScaledResolution sr, Font font, String text) {
        drawSkija(sr, font, text, 1, 1, ColorUtil.active);
    }

    public static Font skija(String name) {
        return skija(name, defaultSize);
    }

    public static Font skija(String name, float size) {
        return load(name, size).skija;
    }

    public static java.awt.Font awt(String name) {
        return awt(name, defaultSize);
    }

    public static java.awt.Font awt(String name, float size) {
        return load(name, size).awt;
    }

    public static FontLoader.LoadedFont load(String name, float size) {
        return FontLoader.loadFont(fontDir + name.toLowerCase() + ".ttf", size);
    }
}
