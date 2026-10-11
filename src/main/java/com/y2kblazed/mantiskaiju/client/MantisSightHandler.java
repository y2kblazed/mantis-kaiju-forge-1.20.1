package com.y2kblazed.mantiskaiju.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.y2kblazed.mantiskaiju.MantisKaijuMod;
import com.y2kblazed.mantiskaiju.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/** Mantis Sight: wear the head + crouch -> see-through outlines on hostile mobs and ores within 20 blocks. */
@Mod.EventBusSubscriber(modid = MantisKaijuMod.MODID, value = Dist.CLIENT)
public final class MantisSightHandler {
    private static final int RADIUS = 20;
    private static final List<BlockPos> ORES = new ArrayList<>();
    private static int scanCooldown;

    private static boolean active(Player p) {
        return p != null && p.isCrouching() && p.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.MANTIS_HEAD.get());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = Minecraft.getInstance().player;
        if (!active(player)) { ORES.clear(); scanCooldown = 0; return; }
        if (--scanCooldown > 0) return;
        scanCooldown = 10; // rescan twice a second
        ORES.clear();
        Level level = player.level();
        BlockPos c = player.blockPosition();
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        int r2 = RADIUS * RADIUS;
        for (int x = -RADIUS; x <= RADIUS; x++)
            for (int y = -RADIUS; y <= RADIUS; y++)
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    if (x * x + y * y + z * z > r2) continue;
                    m.set(c.getX() + x, c.getY() + y, c.getZ() + z);
                    if (level.getBlockState(m).is(Tags.Blocks.ORES)) ORES.add(m.immutable());
                }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Player player = Minecraft.getInstance().player;
        if (!active(player)) return;
        Level level = player.level();
        float pt = event.getPartialTick();
        Vec3 cam = event.getCamera().getPosition();
        PoseStack ps = event.getPoseStack();
        ps.pushPose();
        ps.translate(-cam.x, -cam.y, -cam.z);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.lineWidth(2.5F);
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buf = tess.getBuilder();
        buf.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        for (BlockPos pos : ORES) {
            float[] col = oreColor(level.getBlockState(pos));
            LevelRenderer.renderLineBox(ps, buf, new AABB(pos).inflate(0.002D), col[0], col[1], col[2], 1.0F);
        }

        double r2 = RADIUS * RADIUS;
        for (Entity e : level.getEntities(player, player.getBoundingBox().inflate(RADIUS), en -> en instanceof Enemy && en.isAlive())) {
            if (e.distanceToSqr(player) > r2) continue;
            Vec3 lerp = new Vec3(Mth.lerp(pt, e.xo, e.getX()), Mth.lerp(pt, e.yo, e.getY()), Mth.lerp(pt, e.zo, e.getZ()));
            AABB box = e.getBoundingBox().move(lerp.subtract(e.position()));
            LevelRenderer.renderLineBox(ps, buf, box, 1.0F, 0.15F, 0.15F, 1.0F);
        }

        tess.end();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        ps.popPose();
    }

    private static float[] oreColor(BlockState s) {
        if (s.is(Tags.Blocks.ORES_DIAMOND)) return new float[]{0.3F, 1.0F, 1.0F};
        if (s.is(Tags.Blocks.ORES_EMERALD)) return new float[]{0.2F, 1.0F, 0.3F};
        if (s.is(Tags.Blocks.ORES_GOLD)) return new float[]{1.0F, 0.85F, 0.1F};
        if (s.is(Tags.Blocks.ORES_IRON)) return new float[]{0.85F, 0.7F, 0.6F};
        if (s.is(Tags.Blocks.ORES_REDSTONE)) return new float[]{1.0F, 0.1F, 0.1F};
        if (s.is(Tags.Blocks.ORES_LAPIS)) return new float[]{0.2F, 0.3F, 1.0F};
        if (s.is(Tags.Blocks.ORES_COPPER)) return new float[]{1.0F, 0.5F, 0.2F};
        if (s.is(Tags.Blocks.ORES_NETHERITE_SCRAP)) return new float[]{0.55F, 0.35F, 0.25F};
        if (s.is(Tags.Blocks.ORES_COAL)) return new float[]{0.35F, 0.35F, 0.35F};
        return new float[]{0.8F, 0.4F, 1.0F};
    }

    private MantisSightHandler() {}
}
