package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.material.Fluid;

public class RegistryHelper {
	public static ResourceLocation getRegistryKey(Object obj) {
		if (obj instanceof RegistryNameProvider provider)
			return provider.getRegistryName();
		if (obj instanceof RecipeSerializer serializer)
			return Registry.RECIPE_SERIALIZER.getKey(serializer);
		if (obj instanceof Enchantment enchantment)
			return Registry.ENCHANTMENT.getKey(enchantment);
		if (obj instanceof BlockEntityType blockEntityType)
			return Registry.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
		if (obj instanceof EntityType entityType)
			return Registry.ENTITY_TYPE.getKey(entityType);
		if (obj instanceof Potion potion)
			return Registry.POTION.getKey(potion);
		if (obj instanceof StructureFeature structureFeature)
			return Registry.STRUCTURE_FEATURE.getKey(structureFeature);
		if (obj instanceof Motive motive)
			return Registry.MOTIVE.getKey(motive);
		if (obj instanceof SoundEvent soundEvent)
			return Registry.SOUND_EVENT.getKey(soundEvent);
		if (obj instanceof GameEvent gameEvent)
			return Registry.GAME_EVENT.getKey(gameEvent);
		return null;
	}
}
