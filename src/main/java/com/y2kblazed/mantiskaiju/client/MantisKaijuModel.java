package com.y2kblazed.mantiskaiju.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.y2kblazed.mantiskaiju.MantisKaijuMod;
import com.y2kblazed.mantiskaiju.entity.MantisKaijuEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Praying-mantis body (~2 blocks in model space, scaled x4 by the renderer). */
public class MantisKaijuModel extends EntityModel<MantisKaijuEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(MantisKaijuMod.MODID, "mantis_kaiju"), "main");

    private final ModelPart root, head, leftArm, rightArm, leftClaw, rightClaw, leftWing, rightWing;
    private final ModelPart[] legs = new ModelPart[4];

    public MantisKaijuModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftClaw = leftArm.getChild("left_claw");
        this.rightClaw = rightArm.getChild("right_claw");
        ModelPart abdomen = root.getChild("abdomen");
        this.leftWing = abdomen.getChild("left_wing");
        this.rightWing = abdomen.getChild("right_wing");
        for (int i = 0; i < 4; i++) legs[i] = root.getChild("leg" + i);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();

        PartDefinition abdomen = p.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.0F, -3.0F, 0.0F, 6, 6, 18), PartPose.offsetAndRotation(0.0F, 14.0F, 1.0F, -0.15F, 0.0F, 0.0F));
        abdomen.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 24)
                .addBox(0.0F, -0.25F, 0.0F, 4, 1, 16), PartPose.offsetAndRotation(0.5F, -3.3F, 0.0F, 0.05F, 0.12F, 0.0F));
        abdomen.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 24).mirror()
                .addBox(-4.0F, -0.25F, 0.0F, 4, 1, 16), PartPose.offsetAndRotation(-0.5F, -3.3F, 0.0F, 0.05F, -0.12F, 0.0F));

        p.addOrReplaceChild("thorax", CubeListBuilder.create().texOffs(30, 0)
                .addBox(-2.0F, -2.0F, -11.0F, 4, 4, 11), PartPose.offsetAndRotation(0.0F, 14.0F, 1.0F, -0.9F, 0.0F, 0.0F));

        PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(30, 16).addBox(-4.0F, -4.0F, -3.0F, 8, 5, 4)
                .texOffs(30, 26).addBox(-1.5F, 1.0F, -2.5F, 3, 4, 3)
                .texOffs(46, 26).addBox(-5.5F, -5.0F, -2.5F, 3, 3, 3)
                .texOffs(46, 26).addBox(2.5F, -5.0F, -2.5F, 3, 3, 3),
                PartPose.offset(0.0F, 5.0F, -6.0F));
        head.addOrReplaceChild("left_antenna", CubeListBuilder.create().texOffs(60, 0)
                .addBox(-0.5F, -12.0F, -0.5F, 1, 12, 1), PartPose.offsetAndRotation(1.5F, -4.0F, -2.0F, -0.5F, 0.0F, 0.3F));
        head.addOrReplaceChild("right_antenna", CubeListBuilder.create().texOffs(60, 0)
                .addBox(-0.5F, -12.0F, -0.5F, 1, 12, 1), PartPose.offsetAndRotation(-1.5F, -4.0F, -2.0F, -0.5F, 0.0F, -0.3F));

        PartDefinition leftArm = p.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 42)
                .addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2), PartPose.offsetAndRotation(2.5F, 8.0F, -4.0F, -0.5F, 0.0F, -0.1F));
        leftArm.addOrReplaceChild("left_claw", CubeListBuilder.create().texOffs(8, 42)
                .addBox(-1.0F, 0.0F, -1.0F, 2, 10, 2).texOffs(16, 42).addBox(0.0F, 1.0F, -2.0F, 1, 8, 1),
                PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, -2.6F, 0.0F, 0.0F));
        PartDefinition rightArm = p.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 42).mirror()
                .addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2), PartPose.offsetAndRotation(-2.5F, 8.0F, -4.0F, -0.5F, 0.0F, 0.1F));
        rightArm.addOrReplaceChild("right_claw", CubeListBuilder.create().texOffs(8, 42).mirror()
                .addBox(-1.0F, 0.0F, -1.0F, 2, 10, 2).texOffs(16, 42).addBox(-1.0F, 1.0F, -2.0F, 1, 8, 1),
                PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, -2.6F, 0.0F, 0.0F));

        float[][] legPose = {{2, -1, 0.3F, -0.7F}, {-2, -1, 0.3F, 0.7F}, {2, 3, -0.3F, -0.7F}, {-2, 3, -0.3F, 0.7F}};
        for (int i = 0; i < 4; i++) {
            p.addOrReplaceChild("leg" + i, CubeListBuilder.create().texOffs(24, 42)
                    .addBox(-0.5F, 0.0F, -0.5F, 1, 13, 1),
                    PartPose.offsetAndRotation(legPose[i][0], 15.0F, legPose[i][1], legPose[i][2], 0.0F, legPose[i][3]));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(MantisKaijuEntity e, float limbSwing, float limbSwingAmount, float age, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        head.xRot = headPitch * Mth.DEG_TO_RAD * 0.5F;

        float strike = Mth.sin(this.attackTime * Mth.PI);
        float idle = Mth.sin(age * 0.08F) * 0.06F;
        leftArm.xRot = -0.5F - strike * 1.3F + idle;
        rightArm.xRot = -0.5F - strike * 1.3F - idle;
        leftClaw.xRot = -2.6F + strike * 2.2F;
        rightClaw.xRot = -2.6F + strike * 2.2F;

        float w = Mth.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
        legs[0].xRot = 0.3F + w;
        legs[1].xRot = 0.3F - w;
        legs[2].xRot = -0.3F - w;
        legs[3].xRot = -0.3F + w;

        leftWing.zRot = Mth.sin(age * 0.3F) * 0.04F;
        rightWing.zRot = -leftWing.zRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a) {
        root.render(poseStack, buffer, light, overlay, r, g, b, a);
    }
}
