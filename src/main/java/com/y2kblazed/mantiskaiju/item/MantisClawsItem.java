package com.y2kblazed.mantiskaiju.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/** Curios "gloves" item: reach up to 15 blocks and 2x attack speed while worn. */
public class MantisClawsItem extends Item implements ICurioItem {
    public MantisClawsItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
        // Vanilla survival bases: entity reach 3.0, block reach 4.5 -> both become 15.0
        map.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(uuid, "Mantis claws entity reach", 12.0D, AttributeModifier.Operation.ADDITION));
        map.put(ForgeMod.BLOCK_REACH.get(), new AttributeModifier(uuid, "Mantis claws block reach", 10.5D, AttributeModifier.Operation.ADDITION));
        // MULTIPLY_TOTAL +1.0 = final attack speed x2
        map.put(Attributes.ATTACK_SPEED, new AttributeModifier(uuid, "Mantis claws attack speed", 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return map;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.mantiskaiju.mantis_claws.desc").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
