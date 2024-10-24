package com.jahirtrap.healthindicator.display;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class ParticleRenderer extends Particle {
    private String text;
    private double particleScale;
    private Double animationMinSize, animationMaxSize;
    private Boolean animationFade;

    public ParticleRenderer(ClientLevel clientLevel, double x, double y, double z) {
        super(clientLevel, x, y, z);

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setColor(int color) {
        this.rCol = getRedFromColor(color);
        this.gCol = getGreenFromColor(color);
        this.bCol = getBlueFromColor(color);
    }

    public void setAnimationSize(double min, double max) {
        this.animationMinSize = min;
        this.animationMaxSize = max;
    }

    public void setAnimationFade(boolean animationFade) {
        this.animationFade = animationFade;
    }

    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float partialTicks) {
        if (this.text == null || this.text.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();

        Vec3 cameraPos = camera.getPosition();
        float particleX = (float) (this.xo + (this.x - this.xo) * partialTicks - cameraPos.x());
        float particleY = (float) (this.yo + (this.y - this.yo) * partialTicks - cameraPos.y());
        float particleZ = (float) (this.zo + (this.z - this.zo) * partialTicks - cameraPos.z());

        float textX = (float) (-mc.font.width(this.text) / 2);
        float textY = 0;

        int textColor = getColorFromRGBA(this.rCol, this.gCol, this.bCol, this.alpha);
        int shadowColor = getColorFromRGBA(this.rCol * 0.314F, this.gCol * 0.314F, this.bCol * 0.314F, this.alpha);

        this.animateSize(partialTicks);
        this.animateFade(partialTicks);

        if (this.alpha == 0) return;

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(particleX, particleY, particleZ);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-0.024F, -0.024F, 0.024F);

        var buffer = mc.renderBuffers().bufferSource();

        if (this.particleScale != 0) poseStack.scale((float) this.particleScale, (float) this.particleScale, 1);

        poseStack.translate(0, 0, 1);
        mc.font.drawInBatch(this.text, textX, textY, shadowColor, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        poseStack.translate(0, 0, -2);
        mc.font.drawInBatch(this.text, textX, textY, textColor, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, 15728880);
        poseStack.translate(0, 0, 1);
        poseStack.popPose();

        buffer.endBatch();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public void animateSize(float partialTicks) {
        if (this.animationMinSize == null || this.animationMaxSize == null) return;

        double d1 = 6D * (this.age - 1 + partialTicks) / this.lifetime * (this.animationMaxSize - this.animationMinSize) - this.animationMaxSize + this.animationMinSize;
        double d2 = -3D * (this.age - 1 + partialTicks) / this.lifetime * (this.animationMaxSize - this.animationMinSize) + 2.5D * this.animationMaxSize - 2.5D * this.animationMinSize;
        double d3 = -(d1 + Math.abs(d1)) + 2D * this.animationMaxSize - 2D * this.animationMinSize;
        double d4 = -(d2 + Math.abs(d2)) + 2D * this.animationMaxSize - 2D * this.animationMinSize;
        this.particleScale = -(d3 + Math.abs(d3) + d4 + Math.abs(d4)) / 4D + this.animationMaxSize;
    }

    public void animateFade(float partialTicks) {
        if (!this.animationFade) return;

        double d1 = (this.lifetime - (this.age - 1 + partialTicks)) + this.lifetime / 6D;
        double d2 = (this.lifetime - (this.age - 1 + partialTicks)) - this.lifetime / 6D;
        this.alpha = (float) ((d1 - Math.abs(d2)) / (this.lifetime / 3D));
    }

    public static class DamageParticle extends ParticleRenderer {
        public DamageParticle(ClientLevel clientLevel, double x, double y, double z) {
            super(clientLevel, x, y, z);
            this.tick();
        }

        @Override
        public void tick() {
            if (this.age++ >= this.lifetime) {
                this.remove();
                return;
            }

            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;

            this.yd = (0.12D * (2D * (this.age - 1) - this.lifetime) * (2D * (this.age - 1) - this.lifetime) / (this.lifetime * this.lifetime));
            this.move(this.xd, this.yd, this.zd);
        }
    }
}
