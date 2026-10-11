package com.y2kblazed.mantiskaiju;

import com.y2kblazed.mantiskaiju.entity.MantisKaijuEntity;
import com.y2kblazed.mantiskaiju.registry.ModEntities;
import com.y2kblazed.mantiskaiju.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MantisKaijuMod.MODID)
public class MantisKaijuMod {
    public static final String MODID = "mantiskaiju";

    public MantisKaijuMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(bus);
        ModItems.ITEMS.register(bus);
        bus.addListener(this::onAttributes);
        bus.addListener(this::onTabs);
    }

    private void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MANTIS_KAIJU.get(), MantisKaijuEntity.createAttributes().build());
    }

    private void onTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.MANTIS_KAIJU_SPAWN_EGG);
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.MANTIS_HEAD);
            event.accept(ModItems.MANTIS_CLAWS);
        }
    }
}
