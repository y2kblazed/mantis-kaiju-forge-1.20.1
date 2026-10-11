package com.y2kblazed.mantiskaiju.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.y2kblazed.mantiskaiju.entity.MantisKaijuEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MantisKaijuRenderer extends MobRenderer<MantisKaijuEntity, EntityModel<MantisKaijuEntity>> {
    private static final ResourceLocation SHULKER_TEXTURE = new ResourceLocation("textures/entity/shulker/shulker.png");
    /** Placeholder: End City purpur. Drop a 64x64 PNG at assets/mantiskaiju/textures/entity/mantis_kaiju.png and point this at it. */
    private static final ResourceLocation MANTIS_TEXTURE = new ResourceLocation("textures/block/purpur_block.png");

    private final EntityModel<MantisKaijuEntity> shulkerModel;
    private final EntityModel<MantisKaijuEntity> mantisModel;

    public MantisKaijuRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MantisKaijuModel(ctx.bakeLayer(MantisKaijuModel.LAYER)), 2.5F);
        this.mantisModel = this.model;
        this.shulkerModel = new DormantShulkerModel(ctx.bakeLayer(ModelLayers.SHULKER));
    }

    @Override
    public void render(MantisKaijuEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light) {
        boolean dormant = entity.isDormant();
        this.model = dormant ? shulkerModel : mantisModel;
        this.shadowRadius = dormant ? 0.5F : 2.5F;
        super.render(entity, yaw, partialTicks, poseStack, buffers, light);
    }

    @Override
    protected void scale(MantisKaijuEntity entity, PoseStack poseStack, float partialTicks) {
        if (!entity.isDormant()) {
            // Grows out of the shell over one second
            float t = Mth.clamp((entity.awakenTicks + partialTicks) / 20.0F, 0.0F, 1.0F);
            float s = Mth.lerp(t, 0.3F, 1.0F) * 4.0F;
            poseStack.scale(s, s, s);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MantisKaijuEntity entity) {
        return entity.isDormant() ? SHULKER_TEXTURE : MANTIS_TEXTURE;
    }
}
