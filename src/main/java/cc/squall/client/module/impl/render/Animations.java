package cc.squall.client.module.impl.render;

import cc.squall.client.events.EventRenderItem;
import cc.squall.client.events.EventSwingAnimation;
import cc.squall.client.module.Module;
import cc.squall.client.module.ModuleAnnotation;
import cc.squall.client.module.Setting.Displayable;
import cc.squall.client.module.Setting.impl.EnumSetting;
import cc.squall.client.module.Setting.impl.SliderSetting;
import cc.squall.client.module.Setting.impl.ToggleSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

// credit to rise 5.9 and 6.2, yuri, unlegits other public github client, and openheaven

@ModuleAnnotation
public class Animations extends Module {
    public Animations() {
        super("Animations", "Brings back 1.7 block animations", Category.Render);
    }
    public final EnumSetting<BlockAnimation> blockAnimation = new EnumSetting<>("Block Animation", this, BlockAnimation.None);


    public final EnumSetting<SwingAnimation> swingAnimation = new EnumSetting<>("Swing Animation", this, SwingAnimation.None);

    private final ToggleSetting onlyWhenBlocking = new ToggleSetting("Update Position Only When Blocking", this, true);
    public final SliderSetting swingSpeed = new SliderSetting("Swing Speed", this, 1, -200, 50, 1);
    private final SliderSetting x = new SliderSetting("X", this, 0.0F, -2.0F, 2.0F, 0.05f);
    private final SliderSetting y = new SliderSetting("Y", this, 0.0F, -2.0F, 2.0F, 0.05f);
    private final SliderSetting z = new SliderSetting("Z", this, 0.0F, -2.0F, 2.0F, 0.05f);
    private final SliderSetting scale = new SliderSetting("Scale", this, 1, 0.1, 2, 0.1);
    private final ToggleSetting alwaysShow = new ToggleSetting("Always Show", this, false);
    private final ToggleSetting dontResetBlock = new ToggleSetting("Dont Reset Block", this, false);
    private final ToggleSetting swingEating = new ToggleSetting("Swing While Eating", this, false);
    private final ToggleSetting fluxSwing = new ToggleSetting("Flux Swing", this, false);

    private float blockEase;
    private long circleTicks;

    @Override
    public void onRenderItem(EventRenderItem event) {
        if (event.getItemToRender().getItem() instanceof ItemMap) {
            return;
        }

        if (!onlyWhenBlocking.getValue())

            GlStateManager.translate((float)x.getValue(), (float)y.getValue(), (float)z.getValue());

        double var7 = 0;

        Number scaleValue = scale.getValue();
        var7 = scaleValue.doubleValue();

        final EnumAction itemAction = event.getEnumAction();
        final ItemRenderer itemRenderer = mc.getItemRenderer();
        final boolean flatten = event.isUseItem()
                && (alwaysShow.getValue() || (dontResetBlock.getValue() && itemAction == EnumAction.BLOCK));
        final float animationProgression = flatten ? 0.0F : event.getAnimationProgression();
        final float swingProgress = event.getSwingProgress();
        final float convertedProgress = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

        blockEase += ((event.isUseItem() && itemAction == EnumAction.BLOCK ? 1f : 0f) - blockEase) * 0.35f;

        if (event.isUseItem() && swingEating.getValue()
                && (itemAction == EnumAction.EAT || itemAction == EnumAction.DRINK)) {
            itemRenderer.performDrinking(mc.thePlayer, event.getPartialTicks());
            itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
            GlStateManager.scale(var7, var7, var7);
            event.setCancelled();
            return;
        }

        if (event.isUseItem() && itemAction == EnumAction.BLOCK) {

            if (onlyWhenBlocking.getValue())
                GlStateManager.translate((float)x.getValue(), (float)y.getValue(), (float)z.getValue());

            switch (blockAnimation.getValue()) {

                case None: {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case V1_7: {

                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Sunny: {
                    var7 =.99;
                    GlStateManager.translate(.05f, -.05f, -.12f);
                    itemRenderer.transformFirstPersonItem(animationProgression + 0.15f, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    GlStateManager.translate(-0.5f, 0.2f, 0.0f);

                    break;
                }

                case Lucid: {
                    itemRenderer.transformFirstPersonItem(animationProgression - 0.1F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Astro: {
                    GlStateManager.translate(.0f, .03f, -.05f);
                    itemRenderer.transformFirstPersonItem(animationProgression / 2, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(convertedProgress * 30.0F / 2.0F, -convertedProgress, -0.0F, 9.0F);
                    GlStateManager.rotate(convertedProgress * 40.0F, 1.0F, -convertedProgress / 2.0F, -0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Tap:
                    GL11.glTranslatef(0, 0.3f, 0);
                    float smooth = (swingProgress * 0.8f - (swingProgress * swingProgress) * 0.8f);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(smooth * -90.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.scale(0.37F, 0.37F, 0.37F);
                    itemRenderer.doBlockTransformations();
                    break;

                case Beta:
                    GL11.glTranslatef(0, 0.3f, 0);
                    float var15 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
                    itemRenderer.transformFirstPersonItem(itemRenderer.equippedProgress * 0.5f, 0);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(-var15 * 55 / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-var15 * 45, 1.0F, var15 / 2, -0.0F);
                    itemRenderer.doBlockTransformations();
                    GL11.glTranslated(1.2, 0.3, 0.5);
                    GL11.glTranslatef(-1, this.mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                    break;

                case Slide:
                    GL11.glTranslatef(0, 0.3f, 0);
                    float smooth2 = (swingProgress * 0.8f - (swingProgress * swingProgress) * 0.8f);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.translate(0.0F, itemRenderer.equippedProgress * 0.3 * -0.6F, 0.0F);
                    GlStateManager.rotate(45.0F, 0.0F, 2 + smooth2 * 0.5f, smooth2 * 3);
                    GlStateManager.rotate(0f, 0.0F, 1.0F, 0.0F);
                    GlStateManager.scale(0.37F, 0.37F, 0.37F);
                    itemRenderer.doBlockTransformations();
                    break;

                case Avatar:

                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);

                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
                    float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                    GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(f1 * -40.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(0.4F, 0.4F, 0.4F);

                    itemRenderer.doBlockTransformations();
                    break;

                case Smooth: {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    final float y = -convertedProgress * 2.0F;
                    GlStateManager.translate(0.0F, y / 10.0F + 0.1F, 0.0F);
                    GlStateManager.rotate(y * 10.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(250, 0.2F, 1.0F, -0.6F);
                    GlStateManager.rotate(-10.0F, 1.0F, 0.5F, 1.0F);
                    GlStateManager.rotate(-y * 20.0F, 1.0F, 0.5F, 1.0F);

                    break;
                }

                case Stab: {
                    final float spin = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);

                    GlStateManager.translate(0.6f, 0.3f, -0.6f + -spin * 0.7);
                    GlStateManager.rotate(6090, 0.0f, 0.0f, 0.1f);
                    GlStateManager.rotate(6085, 0.0f, 0.1f, 0.0f);
                    GlStateManager.rotate(6110, 0.1f, 0.0f, 0.0f);
                    itemRenderer.transformFirstPersonItem(0.0F, 0.0f);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    break;
                }

                case Spin: {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0, 0.2F, -1);
                    GlStateManager.rotate(-59, -1, 0, 3);
                    // Don't make the /2 a float it causes the animation to break
                    GlStateManager.rotate(-(System.currentTimeMillis() / 2 % 360), 1, 0, 0.0F);
                    GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                    break;
                }

                case Leaked: {
                    GlStateManager.translate(.0f, -.03f, -.13f);
                    itemRenderer.transformFirstPersonItem(animationProgression / 3F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.0f, 0.1F, 0.0F);
                    itemRenderer.doBlockTransformations();
                    GlStateManager.rotate(convertedProgress * 20.0F / 2.0F, 0.0F, 1.0F, 1.5F);
                    GlStateManager.rotate(-convertedProgress * 200.0F / 4.0F, 1.0f, 0.9F, 0.0F);

                    break;
                }

                case Old: {

                    GlStateManager.translate(0.0F, 0.1F, 0.0F);
                    itemRenderer.transformFirstPersonItem(animationProgression / 2f - 0.2F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Exhibition: {
                    GlStateManager.translate(.0f, -.05f, -0f);
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.0F, 0.3F, -0.0F);
                    GlStateManager.rotate(-convertedProgress * 31.0F, 1.0F, 0.0F, 2.0F);
                    GlStateManager.rotate(-convertedProgress * 33.0F, 1.5F, (convertedProgress / 1.1F), 0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case ExhibitionOld: {
                    GlStateManager.translate(.0f, -.05f, 0f);
                    GlStateManager.translate(-0.04F, 0.13F, 0.0F);
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.5F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(-convertedProgress * 40.0F / 2.0F, convertedProgress / 2.0F, 1.0F, 4.0F);
                    GlStateManager.rotate(-convertedProgress * 30.0F, 1.0F, convertedProgress / 3.0F, -0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case ExhibitionNew: {
                    GlStateManager.translate(.0f, -.04f, -.01f);
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.0F, 0.3F, -0.0F);
                    GlStateManager.rotate(-convertedProgress * 30.0F, 1.0F, 0.0F, 2.0F);
                    GlStateManager.rotate(-convertedProgress * 44.0F, 1.5F, (convertedProgress / 1.2F), 0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Swong: {
                    GlStateManager.translate(.0f, .1f, -.05f);
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(convertedProgress * 30.0F, -convertedProgress, -0.0F, 9.0F);
                    GlStateManager.rotate(convertedProgress * 40.0F, 1.0F, -convertedProgress, -0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Stella: {
                    itemRenderer.transformFirstPersonItem(-0.1F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(-0.5F, 0.4F, -0.2F);
                    GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-70.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(40.0F, 0.0F, 1.0F, 0.0F);
                    break;
                }

                case Flup: {
                    GlStateManager.translate(.0f, .1f, -.05f);
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    GlStateManager.translate(-0.05F, 0.2F, 0.0F);
                    GlStateManager.rotate(-convertedProgress * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-convertedProgress * 70.0F, 1.0F, -0.4F, -0.0F);

                    break;
                }

                case Noov: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 1.5F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    GlStateManager.translate(-0.05F, 0.3F, 0.3F);
                    GlStateManager.rotate(-convertedProgress * 140.0F, 8.0F, 0.0F, 8.0F);
                    GlStateManager.rotate(convertedProgress * 90.0F, 8.0F, 0.0F, 8.0F);

                    break;
                }

                case Komorebi: {
                    itemRenderer.transformFirstPersonItem(-0.25F, 1.0F + convertedProgress / 10.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GL11.glRotated(-convertedProgress * 25.0F, 1.0F, 0.0F, 0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Rhys: {
                    GlStateManager.translate(0.41F, -0.25F, -0.5555557F);
                    GlStateManager.translate(0.0F, 0, 0.0F);
                    GlStateManager.rotate(35.0F, 0f, 1.5F, 0.0F);

                    final float racism = MathHelper.sin(swingProgress * swingProgress / 64 * (float) Math.PI);

                    GlStateManager.rotate(racism * -5.0F, 0.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(convertedProgress * -12.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(convertedProgress * -65.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Swing: {
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    GlStateManager.translate(-0.3F, -0.1F, -0.0F);

                    break;
                }

                case Question: {
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GL11.glTranslatef(-0.35F, 0.1F, 0.0F);
                    GL11.glTranslatef(-0.05F, -0.1F, 0.1F);

                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Expensive: {
                    itemRenderer.transformFirstPersonItem(0.05F, 0.04F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.0F, 0.4F, 0.0F);
                    GlStateManager.rotate(-90.0F * blockEase, 1.0F, 0.0F, 0.0F);

                    break;
                }

                case Inertia: {
                    itemRenderer.transformFirstPersonItem(0.05F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(-0.5F, 0.5F, 0.0F);
                    GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);

                    break;
                }

                case Punch: {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    itemRenderer.doItemUsedTransformations(0.2F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.1F, 0.2F, 0.3F);
                    GlStateManager.rotate(-convertedProgress * 30.0F, -5.0F, 0.0F, 9.0F);
                    GlStateManager.rotate(-convertedProgress * 10.0F, 1.0F, -0.4F, -0.5F);

                    break;
                }

                case Styles: {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    itemRenderer.doItemUsedTransformations(0.2F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(-0.05F, 0.2F, 0.0F);
                    GlStateManager.rotate(-convertedProgress * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-convertedProgress * 70.0F, 1.0F, -0.4F, -0.0F);

                    break;
                }

                case Ethereal: {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    itemRenderer.doItemUsedTransformations(0.2F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(-0.05F, 0.2F, 0.2F);
                    GlStateManager.rotate(-convertedProgress * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-convertedProgress * 70.0F, 1.0F, -0.4F, -0.0F);

                    break;
                }

                case Novoline: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 1.5F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                    GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(-0.05F, 0.3F, 0.3F);
                    GlStateManager.rotate(-convertedProgress * 140.0F, 8.0F, 0.0F, 8.0F);
                    GlStateManager.rotate(convertedProgress * 90.0F, 8.0F, 0.0F, 8.0F);

                    break;
                }

                case Old2: {
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.translate(0.0F, 0.3F, 0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Exhibition2: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(-convertedProgress * 40.0F / 2.0F, convertedProgress / 2.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-convertedProgress * 30.0F, 1.0F, convertedProgress / 2.0F, -0.0F);
                    itemRenderer.doBlockTransformations();
                    GL11.glTranslatef(-0.05F, 0.0F, 0.1F);

                    break;
                }

                case Swing2: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doItemUsedTransformations(0.4F);

                    break;
                }

                case Smooth2: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F - 0.18F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GL11.glRotatef(-convertedProgress * 80.0F / 5.0F, convertedProgress / 3.0F, -0.0F, 9.0F);
                    GL11.glRotatef(-convertedProgress * 40.0F, 8.0F, convertedProgress / 9.0F, -0.1F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Mlodyrackz: {
                    itemRenderer.transformFirstPersonItem(animationProgression * 0.5F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Swang: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GL11.glTranslated(mc.thePlayer.isSneaking() ? -0.03D : 0.1D, 0.0D, 0.1D);
                    GlStateManager.rotate(convertedProgress * 30.0F / 2.0F, -convertedProgress, -0.0F, 9.0F);
                    GlStateManager.rotate(convertedProgress * 40.0F, 1.0F, -convertedProgress / 2.0F, -0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Swong2: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GL11.glTranslated(0.1D, 0.1D, 0.0D);
                    GL11.glRotated(-convertedProgress * 65.0F / 2.0F, -convertedProgress / 2.0F, -0.0F, 9.0F);
                    GL11.glRotated(-convertedProgress * 53.0F, 1.0F, convertedProgress / 2.0F, -0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Swank: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(convertedProgress * 30.0F, -convertedProgress, -0.0F, 9.0F);
                    GlStateManager.rotate(convertedProgress * 40.0F, 1.0F, -convertedProgress, -0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Fixed: {
                    itemRenderer.doItemUsedTransformations(swingProgress);
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Slide2: {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    GL11.glTranslated(-0.3D, 0.3D, 0.0D);
                    GL11.glRotatef(-convertedProgress * 70.0F / 2.0F, -8.0F, 0.0F, 9.0F);
                    GL11.glRotatef(-convertedProgress * 70.0F, 1.0F, -0.4F, -0.0F);

                    break;
                }

                case Jigsaw: {
                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.translate(0.0F, animationProgression * -0.6F, 0.0F);
                    final float jigsaw = swingProgress * 0.8F - swingProgress * swingProgress * 0.8F;
                    GlStateManager.rotate(45.0F, 0.0F, 2.0F + jigsaw * 0.5F, jigsaw * 3.0F);
                    GlStateManager.scale(0.37F, 0.37F, 0.37F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Remix: {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.83F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    final float remix = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.83F);
                    GlStateManager.translate(-0.5F, 0.2F, 0.2F);
                    GlStateManager.rotate(-remix * 43.0F, 58.0F, 23.0F, 45.0F);

                    break;
                }

                case Sigma: {
                    itemRenderer.transformFirstPersonItem(animationProgression * 0.5F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(-convertedProgress * 55.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-convertedProgress * 45.0F, 1.0F, convertedProgress / 2.0F, -0.0F);
                    itemRenderer.doBlockTransformations();
                    GL11.glTranslated(1.2D, 0.3D, 0.5D);
                    GL11.glTranslatef(-1.0F, mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                    GlStateManager.scale(1.2F, 1.2F, 1.2F);

                    break;
                }

                case Jello: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    GL11.glRotatef(-convertedProgress * 40.0F / 4.0F, -(convertedProgress / 2.0F), -0.0F, -15.0F);
                    GL11.glRotatef(-convertedProgress * 30.0F, -1.0F, -(convertedProgress / 2.0F), -0.0F);
                    GlStateManager.rotate(-15.0F, 1.0F, 0.0F, -0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Jello2: {
                    itemRenderer.transformFirstPersonItem(0.0F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    final long now = System.currentTimeMillis();
                    final int pingpong = (int) Math.min(255L, now % 255L > 127L ? Math.abs(now % 255L - 255L) : now % 255L);
                    GlStateManager.translate(0.3F, -0.0F, 0.4F);
                    GlStateManager.translate(0.0F, 0.5F, 0.0F);
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, -1.0F);
                    GlStateManager.translate(0.6F, 0.5F, 0.0F);
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, -1.0F);
                    GlStateManager.rotate(-10.0F, 1.0F, 0.0F, -1.0F);
                    GlStateManager.rotate(mc.thePlayer.isSwingInProgress ? -pingpong / 5.0F : 1.0F, 1.0F, -0.0F, 1.0F);

                    break;
                }

                case Rainy: {
                    itemRenderer.transformFirstPersonItem(animationProgression, 0.83F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    final float rainy = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.83F);
                    GlStateManager.translate(-0.0F, 0.2F, 0.2F);
                    GlStateManager.rotate(-rainy * 43.0F, 58.0F, 23.0F, 45.0F);

                    break;
                }

                case Target: {
                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(convertedProgress * -70.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(0.4F, 0.4F, 0.4F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case LiquidBounce: {
                    itemRenderer.transformFirstPersonItem(animationProgression + 0.1F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    GlStateManager.translate(-0.5F, 0.2F, 0.0F);

                    break;
                }

                case Lucky: {
                    GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.scale(0.4F, 0.4F, 0.4F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    final float lucky = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.83F);
                    GlStateManager.translate(-0.0F, 0.2F, -0.2F);
                    GlStateManager.rotate(-lucky * 7.0F, 58.0F, 23.0F, 45.0F);

                    break;
                }

                case Thinking: {
                    itemRenderer.transformFirstPersonItem(animationProgression * 0.1F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                    final float pulse = swingProgress > 0.5F ? 1.0F - swingProgress : swingProgress;
                    GlStateManager.rotate(-convertedProgress * 55.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-convertedProgress * 45.0F, 1.0F, convertedProgress / 12.0F, -0.0F);
                    GlStateManager.rotate(-pulse * 10.0F, 10.0F, 10.0F, -9.0F);
                    GlStateManager.translate(0.0F, 0.0F, 0.4F);
                    GL11.glTranslated(1.5D, 0.3D, 0.5D);
                    GL11.glTranslatef(-1.0F, mc.thePlayer.isSneaking() ? -0.9F : -0.2F, 0.2F);

                    break;
                }

                case Stitch: {
                    itemRenderer.transformFirstPersonItem(0.1F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();
                    GlStateManager.translate(-0.5F, 0.0F, 0.0F);

                    break;
                }

                case Circle: {
                    ++circleTicks;
                    GlStateManager.translate(-0.0F, -0.2F, -0.6F);
                    GlStateManager.rotate((float) (-circleTicks) * 0.07F * 50.0F, 0.0F, 0.0F, -1.0F);
                    GlStateManager.rotate(44.0F, 0.0F, 1.0F, 0.6F);
                    GlStateManager.rotate(44.0F, 1.0F, 0.0F, -0.6F);
                    GlStateManager.translate(1.0F, -0.2F, 0.5F);
                    GlStateManager.rotate(-44.0F, 1.0F, 0.0F, -0.6F);
                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    GlStateManager.scale(var7, var7, var7);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Swank3: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, swingProgress);
                    GlStateManager.scale(var7, var7, var7);
                    GlStateManager.rotate(convertedProgress * 30.0F, -convertedProgress, -0.0F, 9.0F);
                    GlStateManager.rotate(convertedProgress * 50.0F, 1.0F, -convertedProgress, -0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Swang3: {
                    itemRenderer.transformFirstPersonItem(animationProgression / 2.0F, 0.0F);
                    GlStateManager.scale(var7, var7, var7);
                    final float swang = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
                    GlStateManager.rotate(-swang * 40.0F / 2.0F, swang / 2.0F, -0.0F, 9.0F);
                    GlStateManager.rotate(-swang * 30.0F, 1.0F, swang / 2.0F, -0.0F);
                    itemRenderer.doBlockTransformations();

                    break;
                }

                case Dortware: {
                    final float var1 = MathHelper.sin((float) (swingProgress * swingProgress * Math.PI - 3));
                    final float var = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));

                    itemRenderer.transformFirstPersonItem(animationProgression, 1.0f);

                    GlStateManager.rotate(-var * 10, 0.0f, 15.0f, 200.0f);
                    GlStateManager.rotate(-var * 10f, 300.0f, var / 2.0f, 1.0f);

                    itemRenderer.doBlockTransformations();

                    GL11.glTranslated(2.4, 0.3, 0.5);
                    GL11.glTranslatef(-2.10f, -0.2f, 0.1f);
                    GlStateManager.rotate(var1 * 13.0f, -10.0f, -1.4f, -10.0f);
                }
            }

            event.setCancelled();

        } else if (!event.isUseItem()) {

            if (fluxSwing.getValue()) {
                itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                if (!onlyWhenBlocking.getValue()) {
                    GlStateManager.scale(var7, var7, var7);
                }
                event.setCancelled();
                return;
            }

            switch (swingAnimation.getValue()) {
                case None:
                    itemRenderer.doItemUsedTransformations(swingProgress);
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    if (!onlyWhenBlocking.getValue()) {
                        GlStateManager.scale(var7, var7, var7);
                    }
                    break;

                case Punch: {
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    itemRenderer.doItemUsedTransformations(swingProgress);
                    if (!onlyWhenBlocking.getValue()) {
                        GlStateManager.scale(var7, var7, var7);
                    }
                    break;
                }

                case V1_9Plus: {
                    itemRenderer.doItemUsedTransformations(swingProgress);
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
//                    GlStateManager.translate(0, -((swing - 1) -
//                            (swing == 0 ? 0 : mc.timer.renderPartialTicks)) / 5f, 0);
                    if (!onlyWhenBlocking.getValue()) {
                        GlStateManager.scale(var7, var7, var7);
                    }
                    break;
                }

                case Shove: {
                    itemRenderer.transformFirstPersonItem(animationProgression, animationProgression);
                    itemRenderer.doItemUsedTransformations(swingProgress);
                    if (!onlyWhenBlocking.getValue()) {
                        GlStateManager.scale(var7, var7, var7);
                    }
                    break;
                }

                case Smooth: {
                    itemRenderer.transformFirstPersonItem(animationProgression, swingProgress);
                    itemRenderer.doItemUsedTransformations(animationProgression);
                    if (!onlyWhenBlocking.getValue()) {
                        GlStateManager.scale(var7, var7, var7);
                    }
                    break;
                }
            }

            event.setCancelled();
        }
    }

    @Override
    public void onSwingAnimation(EventSwingAnimation x)  {
        int swingAnimationEnd = x.getAnimationEnd();
        swingAnimationEnd = (int) (swingAnimationEnd * (((float) -swingSpeed.getValue() / 100f) + 1f));
        x.setAnimationEnd(swingAnimationEnd);
    }

    public enum BlockAnimation implements Displayable {
        None("None"),
        V1_7("1.7"),
        Sunny("Sunny"),
        Lucid("Lucid"),
        Astro("Astro"),
        Smooth("Smooth"),
        Spin("Spin"),
        Leaked("Leaked"),
        Old("Old"),
        Exhibition("Exhibition"),
        ExhibitionOld("Exhibition Old"),
        ExhibitionNew("Exhibition New"),
        Swong("Swong"),
        Stella("Stella"),
        Flup("Flup"),
        Noov("Noov"),
        Komorebi("Komorebi"),
        Rhys("Rhys"),
        Swing("Swing"),
        Question("?"),
        Stab("Stab"),
        Beta("Beta"),
        Dortware("Dortware"),
        Avatar("Avatar"),
        Tap("Tap"),
        Expensive("Expensive"),
        Inertia("Inertia"),
        Punch("Punch"),
        Styles("Styles"),
        Ethereal("Ethereal"),
        Novoline("Novoline"),
        Old2("Old2"),
        Exhibition2("Exhibition2"),
        Swing2("Swing2"),
        Smooth2("Smooth2"),
        Mlodyrackz("Mlodyrackz"),
        Swang("Swang"),
        Swong2("Swong2"),
        Swank("Swank"),
        Fixed("Fixed"),
        Slide("Slide"),
        Slide2("Slide2"),
        Jigsaw("Jigsaw"),
        Remix("Remix"),
        Sigma("Sigma"),
        Jello("Jello"),
        Jello2("Jello2"),
        Rainy("Rainy"),
        Target("Target"),
        LiquidBounce("LiquidBounce"),
        Lucky("Lucky"),
        Thinking("Thinking"),
        Stitch("Stitch"),
        Circle("Circle"),
        Swank3("Swank3"),
        Swang3("Swang3");

        private final String displayName;

        BlockAnimation(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }
    }

    public enum SwingAnimation implements Displayable {
        None("None"),
        Punch("Punch"),
        Shove("Shove"),
        Smooth("Smooth"),
        V1_9Plus("1.9+");

        private final String displayName;

        SwingAnimation(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }
    }
}
