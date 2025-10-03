package com.jahirtrap.healthindicator.display;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.ParticleGroupRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static com.jahirtrap.healthindicator.HealthIndicatorMod.MODID;
import static com.jahirtrap.healthindicator.util.CommonUtils.*;

public class ParticleRenderer extends Particle {
    public static final ParticleRenderType CUSTOM = new ParticleRenderType(MODID + ":particle_renderer");
    private float rCol = 1;
    private float gCol = 1;
    private float bCol = 1;
    private float alpha = 1;
    private String text;
    private double scale = 1;
    private double animationScale;
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

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setAnimationSize(double min, double max) {
        this.animationMinSize = min;
        this.animationMaxSize = max;
    }

    public void setAnimationFade(boolean animationFade) {
        this.animationFade = animationFade;
    }

    @Override
    public @NotNull ParticleRenderType getGroup() {
        return CUSTOM;
    }

    public void animateSize(float partialTicks) {
        if (this.animationMinSize == null || this.animationMaxSize == null) return;

        double d1 = 6D * (this.age - 1 + partialTicks) / this.lifetime * (this.animationMaxSize - this.animationMinSize) - this.animationMaxSize + this.animationMinSize;
        double d2 = -3D * (this.age - 1 + partialTicks) / this.lifetime * (this.animationMaxSize - this.animationMinSize) + 2.5D * this.animationMaxSize - 2.5D * this.animationMinSize;
        double d3 = -(d1 + Math.abs(d1)) + 2D * this.animationMaxSize - 2D * this.animationMinSize;
        double d4 = -(d2 + Math.abs(d2)) + 2D * this.animationMaxSize - 2D * this.animationMinSize;
        this.animationScale = -(d3 + Math.abs(d3) + d4 + Math.abs(d4)) / 4D + this.animationMaxSize;
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

    public static class ParticleRendererGroup extends ParticleGroup<ParticleRenderer> {
        public ParticleRendererGroup(ParticleEngine engine) {
            super(engine);
        }

        public static final class Entry {
            public final Matrix4f pose;
            public final String text;
            public final int color;
            public final float scale;

            Entry(Matrix4f pose, String text, int color, float scale) {
                this.pose = pose;
                this.text = text;
                this.color = color;
                this.scale = scale;
            }
        }

        public record State(List<Entry> entries) implements ParticleGroupRenderState {
            @Override
            public void submit(SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
                var mc = Minecraft.getInstance();

                for (Entry entry : entries) {
                    if (entry.text == null || entry.text.isEmpty()) continue;

                    float textX = (float) (-mc.font.width(entry.text) / 2);
                    float textY = -mc.font.wordWrapHeight(entry.text, 0);

                    PoseStack poseStack = new PoseStack();
                    poseStack.last().pose().set(entry.pose);
                    poseStack.scale(entry.scale, entry.scale, 1);

                    collector.submitText(poseStack, textX, textY, Component.literal(entry.text).getVisualOrderText(), true, Font.DisplayMode.NORMAL, 0x00F000F0, entry.color, 0, 0);
                }
            }
        }


        @Override
        public @NotNull ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTick) {
            List<Entry> entries = new ArrayList<>(this.particles.size());
            Vec3 pos = camera.getPosition();

            for (ParticleRenderer p : this.particles) {
                if (p.alpha == 0) continue;

                float x = (float) (p.xo + (p.x - p.xo) * partialTick - pos.x());
                float y = (float) (p.yo + (p.y - p.yo) * partialTick - pos.y());
                float z = (float) (p.zo + (p.z - p.zo) * partialTick - pos.z());

                int color = getColorFromRGBA(p.rCol, p.gCol, p.bCol, p.alpha);

                p.animateSize(partialTick);
                p.animateFade(partialTick);

                Matrix4f matrix = new Matrix4f()
                        .translate(x, y, z)
                        .rotate(camera.rotation())
                        .rotate((float) Math.PI, 0, 1, 0)
                        .scale(-0.024f, -0.024f, -0.024f);

                float scale = (float) ((p.animationScale != 0 ? p.animationScale : 1) * p.scale);
                entries.add(new Entry(matrix, p.text, color, scale));
            }

            return new State(entries);
        }
    }
}
