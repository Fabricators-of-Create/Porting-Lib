package io.github.fabricators_of_create.porting_lib.data;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public abstract class ModdedEntityLootSubProvider implements LootTableSubProvider {
	private static final Set<EntityType<?>> SPECIAL_LOOT_TABLE_TYPES = ImmutableSet.of(
			EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER
	);
	protected final HolderLookup.Provider registries;
	private final FeatureFlagSet allowed;
	private final FeatureFlagSet required;
	private final Map<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>> map = Maps.newHashMap();

	protected final AnyOfCondition.Builder shouldSmeltLoot() {
		HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
		return AnyOfCondition.anyOf(
				LootItemEntityPropertyCondition.hasProperties(
						LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))
				),
				LootItemEntityPropertyCondition.hasProperties(
						LootContext.EntityTarget.DIRECT_ATTACKER,
						EntityPredicate.Builder.entity()
								.equipment(
										EntityEquipmentPredicate.Builder.equipment()
												.mainhand(
														ItemPredicate.Builder.item()
																.withSubPredicate(
																		ItemSubPredicates.ENCHANTMENTS,
																		ItemEnchantmentsPredicate.enchantments(
																				List.of(new EnchantmentPredicate(registrylookup.getOrThrow(EnchantmentTags.SMELTS_LOOT), MinMaxBounds.Ints.ANY))
																		)
																)
												)
								)
				)
		);
	}

	protected ModdedEntityLootSubProvider(FeatureFlagSet required, HolderLookup.Provider registries) {
		this(required, required, registries);
	}

	protected ModdedEntityLootSubProvider(FeatureFlagSet allowed, FeatureFlagSet required, HolderLookup.Provider registries) {
		this.allowed = allowed;
		this.required = required;
		this.registries = registries;
	}

	protected static LootTable.Builder createSheepTable(ItemLike item) {
		return LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(item)))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(NestedLootTable.lootTableReference(EntityType.SHEEP.getDefaultLootTable())));
	}

	public abstract void generate();

	protected Stream<EntityType<?>> getKnownEntityTypes() {
		return BuiltInRegistries.ENTITY_TYPE.stream();
	}

	@Override
	public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> p_251751_) {
		this.generate();
		Set<ResourceKey<LootTable>> set = new HashSet<>();
		this.getKnownEntityTypes()
				.map(EntityType::builtInRegistryHolder)
				.forEach(
						p_266624_ -> {
							EntityType<?> entitytype = p_266624_.value();
							if (entitytype.isEnabled(this.allowed)) {
								if (canHaveLootTable(entitytype)) {
									Map<ResourceKey<LootTable>, LootTable.Builder> tables = this.map.remove(entitytype);
									ResourceKey<LootTable> resourcekey = entitytype.getDefaultLootTable();
									if (resourcekey != BuiltInLootTables.EMPTY && entitytype.isEnabled(this.required) && (tables == null || !tables.containsKey(resourcekey))
									)
									{
										throw new IllegalStateException(
												String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourcekey, p_266624_.key().location())
										);
									}

									if (tables != null) {
										tables.forEach(
												(location, builder) -> {
													if (!set.add(location)) {
														throw new IllegalStateException(
																String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", location, p_266624_.key().location())
														);
													} else {
														p_251751_.accept(location, builder);
													}
												}
										);
									}
								} else {
									Map<ResourceKey<LootTable>, LootTable.Builder> map1 = this.map.remove(entitytype);
									if (map1 != null) {
										throw new IllegalStateException(
												String.format(
														Locale.ROOT,
														"Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
														map1.keySet().stream().map(key -> key.location().toString()).collect(Collectors.joining(",")),
														p_266624_.key().location()
												)
										);
									}
								}
							}
						}
				);
		if (!this.map.isEmpty()) {
			throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
		}
	}

	protected boolean canHaveLootTable(EntityType<?> type) {
		return SPECIAL_LOOT_TABLE_TYPES.contains(type) || type.getCategory() != MobCategory.MISC;
	}

	protected LootItemCondition.Builder killedByFrog() {
		return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG)));
	}

	protected LootItemCondition.Builder killedByFrogVariant(ResourceKey<FrogVariant> key) {
		return DamageSourceCondition.hasDamageSource(
				DamageSourcePredicate.Builder.damageType()
						.source(
								EntityPredicate.Builder.entity()
										.of(EntityType.FROG)
										.subPredicate(EntitySubPredicates.frogVariant(BuiltInRegistries.FROG_VARIANT.getHolderOrThrow(key)))
						)
		);
	}

	protected void add(EntityType<?> type, LootTable.Builder builder) {
		this.add(type, type.getDefaultLootTable(), builder);
	}

	protected void add(EntityType<?> type, ResourceKey<LootTable> key, LootTable.Builder builder) {
		this.map.computeIfAbsent(type, t -> new HashMap<>()).put(key, builder);
	}
}
