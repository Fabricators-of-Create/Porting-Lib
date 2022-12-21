package io.github.fabricators_of_create.porting_lib.data;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class ModdedEntityLootSubProvider extends EntityLootSubProvider {

	protected ModdedEntityLootSubProvider(FeatureFlagSet featureFlagSet) {
		super(featureFlagSet);
	}

	@Override
	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
		this.generate();
		Set<ResourceLocation> set = Sets.newHashSet();
		for(EntityType<?> entityType : getKnownEntities()) {
			if (entityType.isEnabled(this.enabledFeatures)) {
				Map<ResourceLocation, LootTable.Builder> map;
				if (isNonLiving(entityType)) {
					map = this.map.remove(entityType);
					ResourceLocation resourceLocation = entityType.getDefaultLootTable();
					if (!resourceLocation.equals(BuiltInLootTables.EMPTY) && (map == null || !map.containsKey(resourceLocation))) {
						throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourceLocation, BuiltInRegistries.ENTITY_TYPE.getKey(entityType)));
					}

					if (map != null) {
						map.forEach((resourceLocationx, builder) -> {
							if (!set.add(resourceLocationx)) {
								throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", resourceLocationx, BuiltInRegistries.ENTITY_TYPE.getKey(entityType)));
							} else {
								biConsumer.accept(resourceLocationx, builder);
							}
						});
					}
				} else {
					map = (Map)this.map.remove(entityType);
					if (map != null) {
						throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", map.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")), BuiltInRegistries.ENTITY_TYPE.getKey(entityType)));
					}
				}

			}
		}
		if (!this.map.isEmpty()) {
			throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
		}
	}

	protected Iterable<EntityType<?>> getKnownEntities() {
		return BuiltInRegistries.ENTITY_TYPE;
	}

	protected boolean isNonLiving(EntityType<?> entitytype) {
		return !SPECIAL_LOOT_TABLE_TYPES.contains(entitytype) && entitytype.getCategory() == MobCategory.MISC;
	}
}
