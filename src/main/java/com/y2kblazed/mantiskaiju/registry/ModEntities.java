package com.y2kblazed.mantiskaiju.registry;

import com.y2kblazed.mantiskaiju.MantisKaijuMod;
import com.y2kblazed.mantiskaiju.entity.MantisKaijuEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MantisKaijuMod.MODID);

    public static final RegistryObject<EntityType<MantisKaijuEntity>> MANTIS_KAIJU = ENTITIES.register("mantis_kaiju",
            () -> EntityType.Builder.<MantisKaijuEntity>of(MantisKaijuEntity::new, MobCategory.MONSTER)
                    .sized(1.0F, 1.0F) // dormant shulker size; grows when awakened
                    .fireImmune()
                    .clientTrackingRange(16)
                    .build("mantis_kaiju"));

    private ModEntities() {}
}
