package com.y2kblazed.mantiskaiju.registry;

import com.y2kblazed.mantiskaiju.MantisKaijuMod;
import com.y2kblazed.mantiskaiju.item.MantisClawsItem;
import com.y2kblazed.mantiskaiju.item.MantisHeadItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MantisKaijuMod.MODID);

    public static final RegistryObject<Item> MANTIS_HEAD = ITEMS.register("mantis_head",
            () -> new MantisHeadItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    public static final RegistryObject<Item> MANTIS_CLAWS = ITEMS.register("mantis_claws",
            () -> new MantisClawsItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    public static final RegistryObject<Item> MANTIS_KAIJU_SPAWN_EGG = ITEMS.register("mantis_kaiju_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MANTIS_KAIJU, 0x946994, 0xE6DCA0, new Item.Properties()));

    private ModItems() {}
}
