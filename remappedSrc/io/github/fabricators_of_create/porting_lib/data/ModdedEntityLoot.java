package io.github.fabricators_of_create.porting_lib.data;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.data.server.EntityLootTableGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class ModdedEntityLoot extends EntityLootTableGenerator {
	protected abstract void addTables();

	@Override
	public void accept(BiConsumer<Identifier, LootTable.Builder> p_124377_) {
		this.addTables();
		Set<Identifier> set = Sets.newHashSet();

		for(EntityType<?> entitytype : getKnownEntities()) {
			Identifier resourcelocation = entitytype.getLootTableId();
			if (isNonLiving(entitytype)) {
				if (resourcelocation != LootTables.EMPTY && this.lootTables.remove(resourcelocation) != null) {
					throw new IllegalStateException(String.format("Weird loottable '%s' for '%s', not a LivingEntity so should not have loot", resourcelocation, Registry.ENTITY_TYPE.getId(entitytype)));
				}
			} else if (resourcelocation != LootTables.EMPTY && set.add(resourcelocation)) {
				LootTable.Builder loottable$builder = this.lootTables.remove(resourcelocation);
				if (loottable$builder == null) {
					throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", resourcelocation, Registry.ENTITY_TYPE.getId(entitytype)));
				}

				p_124377_.accept(resourcelocation, loottable$builder);
			}
		}

		this.lootTables.forEach(p_124377_);
	}

	protected Iterable<EntityType<?>> getKnownEntities() {
		return Registry.ENTITY_TYPE;
	}

	protected boolean isNonLiving(EntityType<?> entitytype) {
		return !ENTITY_TYPES_IN_MISC_GROUP_TO_CHECK.contains(entitytype) && entitytype.getSpawnGroup() == SpawnGroup.MISC;
	}
}
