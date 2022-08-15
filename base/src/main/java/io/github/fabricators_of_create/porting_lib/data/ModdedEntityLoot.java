package io.github.fabricators_of_create.porting_lib.data;

import com.google.common.collect.Sets;

import net.minecraft.core.Registry;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Set;
import java.util.function.BiConsumer;

public abstract class ModdedEntityLoot extends EntityLoot {
	protected abstract void addTables();

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_124377_) {
		this.addTables();
		Set<ResourceLocation> set = Sets.newHashSet();

		for(EntityType<?> entitytype : getKnownEntities()) {
			ResourceLocation resourcelocation = entitytype.getDefaultLootTable();
			if (isNonLiving(entitytype)) {
				if (resourcelocation != BuiltInLootTables.EMPTY && this.map.remove(resourcelocation) != null) {
					throw new IllegalStateException(String.format("Weird loottable '%s' for '%s', not a LivingEntity so should not have loot", resourcelocation, Registry.ENTITY_TYPE.getKey(entitytype)));
				}
			} else if (resourcelocation != BuiltInLootTables.EMPTY && set.add(resourcelocation)) {
				LootTable.Builder loottable$builder = this.map.remove(resourcelocation);
				if (loottable$builder == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation, Registry.ENTITY_TYPE.getKey(entitytype)));
				}

				p_124377_.accept(resourcelocation, loottable$builder);
			}
		}

		this.map.forEach(p_124377_);
	}

	protected Iterable<EntityType<?>> getKnownEntities() {
		return Registry.ENTITY_TYPE;
	}

	protected boolean isNonLiving(EntityType<?> entitytype) {
		return !SPECIAL_LOOT_TABLE_TYPES.contains(entitytype) && entitytype.getCategory() == MobCategory.MISC;
	}
}
