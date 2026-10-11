package com.y2kblazed.mantiskaiju.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.y2kblazed.mantiskaiju.entity.MantisKaijuEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/** Re-uses the vanilla shulker mesh so the dormant kaiju looks exactly like a regular shulker. */
public class DormantShulkerModel extends EntityModel<MantisKaijuEntity> {
    private final ModelPart root;
    private final ModelPart lid;

    public DormantShulkerModel(ModelPart root) {
        this.root = root;
        this.lid = root.getChild("lid");
    }

    @Override
    public void setupAnim(MantisKaijuEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Tiny peek every few seconds, like a real shulker
        float peek = Math.max(0.0F, Mth.sin(ageInTicks * 0.05F)) * 2.0F;
        this.lid.setPos(0.0F, 24.0F - peek, 0.0F);
        this.lid.yRot = 0.0F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        this.root.render(poseStack, buffer, light, overlay, r, g, b, a);
    }
}
