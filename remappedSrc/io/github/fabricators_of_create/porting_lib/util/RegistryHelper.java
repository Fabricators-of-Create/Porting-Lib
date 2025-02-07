package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.feature.StructureFeature;

public class RegistryHelper {
	public static Identifier getRegistryKey(Object obj) {
		if (obj instanceof RegistryNameProvider provider)
			return provider.getRegistryName();
		if (obj instanceof RecipeSerializer serializer)
			return Registry.RECIPE_SERIALIZER.getId(serializer);
		if (obj instanceof Enchantment enchantment)
			return Registry.ENCHANTMENT.getId(enchantment);
		if (obj instanceof BlockEntityType blockEntityType)
			return Registry.BLOCK_ENTITY_TYPE.getId(blockEntityType);
		if (obj instanceof EntityType entityType)
			return Registry.ENTITY_TYPE.getId(entityType);
		if (obj instanceof Potion potion)
			return Registry.POTION.getId(potion);
		if (obj instanceof StructureFeature structureFeature)
			return Registry.STRUCTURE_FEATURE.getId(structureFeature);
		if (obj instanceof PaintingMotive motive)
			return Registry.PAINTING_MOTIVE.getId(motive);
		if (obj instanceof SoundEvent soundEvent)
			return Registry.SOUND_EVENT.getId(soundEvent);
		if (obj instanceof GameEvent gameEvent)
			return Registry.GAME_EVENT.getId(gameEvent);
		return null;
	}
}
