package com.y2kblazed.mantiskaiju.client;

import com.y2kblazed.mantiskaiju.MantisKaijuMod;
import com.y2kblazed.mantiskaiju.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MantisKaijuMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MANTIS_KAIJU.get(), MantisKaijuRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MantisKaijuModel.LAYER, MantisKaijuModel::createBodyLayer);
    }

    private ClientModEvents() {}
}
