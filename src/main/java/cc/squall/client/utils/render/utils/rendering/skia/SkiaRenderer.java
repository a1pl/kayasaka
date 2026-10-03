package cc.squall.client.utils.render.utils.rendering.skia;

import io.github.humbleui.skija.BackendRenderTarget;
import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.ColorType;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.skija.SurfaceOrigin;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjglx.opengl.GLContext;

import java.util.function.Consumer;

// w github search
public final class SkiaRenderer {

    private static final int SAMPLER_UNITS = 16;

    private static DirectContext context;
    private static BackendRenderTarget target;
    private static Surface surface;
    private static int surfaceFbo = -1;
    private static int surfaceWidth;
    private static int surfaceHeight;

    private SkiaRenderer() {}

    public static void draw(int width, int height, float scale, Consumer<Canvas> drawer) {
        int prevFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int prevProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        int prevVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int prevArrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        int prevElementBuffer = GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        int prevActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushClientAttrib(GL11.GL_CLIENT_ALL_ATTRIB_BITS);
        try {
            if (context == null) {
                context = DirectContext.makeGL();
            }
            ensureSurface(prevFbo, width, height);

            context.resetGLAll();

            Canvas canvas = surface.getCanvas();
            canvas.save();
            canvas.scale(scale, scale);
            try {
                drawer.accept(canvas);
            } finally {
                canvas.restore();
                context.flushAndSubmit(surface);
            }
        } finally {
            if (GLContext.getCapabilities().OpenGL33) {
                for (int unit = 0; unit < SAMPLER_UNITS; unit++) {
                    GL33.glBindSampler(unit, 0);
                }
            }
            GL20.glUseProgram(prevProgram);
            GL30.glBindVertexArray(prevVao);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, prevArrayBuffer);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, prevElementBuffer);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFbo);
            GL13.glActiveTexture(prevActiveTexture);
            GL11.glPopClientAttrib();
            GL11.glPopAttrib();
        }
    }

    private static void ensureSurface(int fbo, int width, int height) {
        if (surface != null && fbo == surfaceFbo && width == surfaceWidth && height == surfaceHeight) {
            return;
        }

        if (surface != null) surface.close();
        if (target != null) target.close();

        target = BackendRenderTarget.makeGL(width, height, 0, 0, fbo, GL11.GL_RGBA8);
        surface = Surface.makeFromBackendRenderTarget(context, target, SurfaceOrigin.BOTTOM_LEFT, ColorType.RGBA_8888, null);
        surfaceFbo = fbo;
        surfaceWidth = width;
        surfaceHeight = height;
    }
}
