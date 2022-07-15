package io.github.fabricators_of_create.porting_lib.util.client;

import io.github.fabricators_of_create.porting_lib.util.ArmorTextureItem;
import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class ClientHooks {
	public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlot slot, String type) {
		String result = null;
		if (armor.getItem() instanceof ArmorTextureItem armorTextureItem)
			result = armorTextureItem.getArmorTexture(armor, entity, slot, type);
		return result != null ? result : _default;
	}

	public static void registerFluidVariantsFromAttributes(Fluid fluid, FluidAttributes attributes) {
		FluidRenderHandlerRegistry.INSTANCE.register(fluid, new FluidAttributeRenderHandler(attributes));
	}
}
