package com.y2kblazed.mantiskaiju.world;

import com.y2kblazed.mantiskaiju.MantisKaijuMod;
import com.y2kblazed.mantiskaiju.entity.MantisKaijuEntity;
import com.y2kblazed.mantiskaiju.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * When a new End City generates, finds its highest piece and places a dormant Mantis Kaiju on top of it.
 */
@Mod.EventBusSubscriber(modid = MantisKaijuMod.MODID)
public final class EndCityKaijuSpawner {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> PENDING = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !event.isNewChunk()) return;
        if (level.dimension() != Level.END) return;
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        for (Map.Entry<Structure, StructureStart> entry : event.getChunk().getAllStarts().entrySet()) {
            if (!BuiltinStructures.END_CITY.location().equals(registry.getKey(entry.getKey()))) continue;
            StructureStart start = entry.getValue();
            if (!start.isValid()) continue;
            start.getPieces().stream()
                    .max(Comparator.comparingInt(p -> p.getBoundingBox().maxY()))
                    .ifPresent(top -> {
                        BoundingBox bb = top.getBoundingBox();
                        BlockPos c = bb.getCenter();
                        PENDING.computeIfAbsent(level.dimension(), k -> ConcurrentHashMap.newKeySet())
                                .add(new BlockPos(c.getX(), bb.maxY(), c.getZ()));
                    });
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        Set<BlockPos> set = PENDING.get(level.dimension());
        if (set == null || set.isEmpty()) return;
        Iterator<BlockPos> it = set.iterator();
        while (it.hasNext()) {
            BlockPos top = it.next();
            if (!level.isPositionEntityTicking(top)) continue; // wait until the top piece's chunk is fully loaded
            it.remove();
            BlockPos spot = findStandingSpot(level, top);
            if (spot == null) continue;
            MantisKaijuEntity kaiju = ModEntities.MANTIS_KAIJU.get().create(level);
            if (kaiju == null) continue;
            kaiju.moveTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, 0.0F, 0.0F);
            kaiju.setPersistenceRequired();
            level.addFreshEntity(kaiju);
        }
    }

    /** Highest air block with a solid block beneath, searching outward from the piece centre. */
    private static BlockPos findStandingSpot(ServerLevel level, BlockPos top) {
        for (int r = 0; r <= 4; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != r) continue;
                    for (int y = top.getY() + 2; y > top.getY() - 24; y--) {
                        BlockPos p = new BlockPos(top.getX() + dx, y, top.getZ() + dz);
                        if (level.getBlockState(p).isAir() && level.getBlockState(p.above()).isAir()
                                && level.getBlockState(p.below()).isFaceSturdy(level, p.below(), net.minecraft.core.Direction.UP)) {
                            return p;
                        }
                    }
                }
            }
        }
        return null;
    }

    private EndCityKaijuSpawner() {}
}
