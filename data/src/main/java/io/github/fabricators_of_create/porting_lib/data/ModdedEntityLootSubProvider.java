package io.github.fabricators_of_create.porting_lib.data;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
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

	private final FeatureFlagSet allowed;
	private final FeatureFlagSet required;

	public ModdedEntityLootSubProvider(FeatureFlagSet enabledFeatures) {
		this(enabledFeatures, enabledFeatures);
	}

	public ModdedEntityLootSubProvider(FeatureFlagSet allowed, FeatureFlagSet required) {
		super(allowed, required);
		this.allowed = allowed;
		this.required = required;
	}

	public void generate(BiConsumer<ResourceLocation, LootTable.Builder> output) {
		this.generate();
		Set<ResourceLocation> set = Sets.newHashSet();
		this.getKnownEntityTypes().map(EntityType::builtInRegistryHolder).forEach((entityTypeReference) -> {
			EntityType<?> entitytype = entityTypeReference.value();
			if (entitytype.isEnabled(this.allowed)) {
				if (canHaveLootTable(entitytype)) {
					Map<ResourceLocation, LootTable.Builder> map = this.map.remove(entitytype);
					ResourceLocation resourcelocation = entitytype.getDefaultLootTable();
					if (!resourcelocation.equals(BuiltInLootTables.EMPTY) && entitytype.isEnabled(this.required) && (map == null || !map.containsKey(resourcelocation))) {
						throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourcelocation, entityTypeReference.key().location()));
					}

					if (map != null) {
						map.forEach((location, builder) -> {
							if (!set.add(location)) {
								throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", location, entityTypeReference.key().location()));
							} else {
								output.accept(location, builder);
							}
						});
					}
				} else {
					Map<ResourceLocation, LootTable.Builder> map1 = this.map.remove(entitytype);
					if (map1 != null) {
						throw new IllegalStateException(String.format(Locale.ROOT, "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot", map1.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(",")), entityTypeReference.key().location()));
					}
				}

			}
		});
		if (!this.map.isEmpty()) {
			throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
		}
	}

	protected Stream<EntityType<?>> getKnownEntityTypes() {
		return BuiltInRegistries.ENTITY_TYPE.stream();
	}

	private static boolean canHaveLootTable(EntityType<?> entityType) {
		return SPECIAL_LOOT_TABLE_TYPES.contains(entityType) || entityType.getCategory() != MobCategory.MISC;
	}
}
