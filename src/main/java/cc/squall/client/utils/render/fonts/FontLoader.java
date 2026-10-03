package cc.squall.client.utils.render.fonts;

import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontEdging;
import io.github.humbleui.skija.FontHinting;
import io.github.humbleui.skija.FontMgr;
import io.github.humbleui.skija.Typeface;

import java.awt.FontFormatException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public final class FontLoader {

    public static final class LoadedFont {
        public final java.awt.Font awt;
        public final Font skija;

        LoadedFont(java.awt.Font awt, Font skija) {
            this.awt = awt;
            this.skija = skija;
        }
    }

    private static final Map<String, java.awt.Font> awtBases = new HashMap<>();
    private static final Map<String, Typeface> typefaces = new HashMap<>();
    private static final Map<String, LoadedFont> cache = new HashMap<>();

    private FontLoader() {}

    public static LoadedFont loadFont(String path, float size) {
        String key = path + "@" + size;
        LoadedFont cached = cache.get(key);
        if (cached != null) return cached;

        ensureBase(path);
        // hinted metrics round every advance to a whole pixel, which bunches some
        // letter pairs and gaps others. linear metrics + subpixel keeps them exact
        Font skija = new Font(typefaces.get(path), size)
                .setSubpixel(true)
                .setMetricsLinear(true)
                .setHinting(FontHinting.NONE)
                .setEdging(FontEdging.ANTI_ALIAS);
        LoadedFont loaded = new LoadedFont(awtBases.get(path).deriveFont(size), skija);
        cache.put(key, loaded);
        return loaded;
    }

    private static void ensureBase(String path) {
        if (typefaces.containsKey(path)) return;

        byte[] bytes = readResource(path);

        try {
            awtBases.put(path, java.awt.Font.createFont(
                    java.awt.Font.TRUETYPE_FONT, new ByteArrayInputStream(bytes)));
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException("AWT couldn't parse font: " + path, e);
        }

        Typeface tf = FontMgr.getDefault().makeFromData(Data.makeFromBytes(bytes));
        if (tf == null) throw new RuntimeException("Skija couldn't parse font: " + path);
        typefaces.put(path, tf);
    }

    private static byte[] readResource(String path) {
        try (InputStream in = FontLoader.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalArgumentException("Font not found on classpath: " + path);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
