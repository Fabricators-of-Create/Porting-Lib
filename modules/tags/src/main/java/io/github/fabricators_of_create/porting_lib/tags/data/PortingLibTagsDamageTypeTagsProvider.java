package io.github.fabricators_of_create.porting_lib.tags.data;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.concurrent.CompletableFuture;

public class PortingLibTagsDamageTypeTagsProvider extends FabricTagProvider<DamageType> {

	public PortingLibTagsDamageTypeTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, Registries.DAMAGE_TYPE, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		tag(DamageTypes.WITHER, Tags.DamageTypes.IS_WITHER);
		tag(DamageTypes.WITHER_SKULL, Tags.DamageTypes.IS_WITHER);

		tag(DamageTypes.MAGIC, Tags.DamageTypes.IS_MAGIC);
		tag(DamageTypes.INDIRECT_MAGIC, Tags.DamageTypes.IS_MAGIC);
		tag(DamageTypes.THORNS, Tags.DamageTypes.IS_MAGIC);
		tag(DamageTypes.DRAGON_BREATH, Tags.DamageTypes.IS_MAGIC);
		tag(Tags.DamageTypes.IS_MAGIC).addTag(Tags.DamageTypes.IS_POISON).addTag(Tags.DamageTypes.IS_WITHER);

		tag(DamageTypes.IN_FIRE, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.ON_FIRE, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.LAVA, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.HOT_FLOOR, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.DROWN, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.STARVE, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.DRY_OUT, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.FREEZE, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.LIGHTNING_BOLT, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.CACTUS, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.STALAGMITE, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.FALLING_STALACTITE, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.FALLING_BLOCK, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.FALLING_ANVIL, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.CRAMMING, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.FLY_INTO_WALL, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.SWEET_BERRY_BUSH, Tags.DamageTypes.IS_ENVIRONMENT);
		tag(DamageTypes.IN_WALL, Tags.DamageTypes.IS_ENVIRONMENT);

		tag(DamageTypes.CACTUS, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.STALAGMITE, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.FALLING_STALACTITE, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.FALLING_BLOCK, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.FALLING_ANVIL, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.CRAMMING, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.FLY_INTO_WALL, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.SWEET_BERRY_BUSH, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.FALL, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.STING, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.MOB_ATTACK, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.PLAYER_ATTACK, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.MOB_ATTACK_NO_AGGRO, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.ARROW, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.THROWN, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.TRIDENT, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.MOB_PROJECTILE, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.SONIC_BOOM, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.IN_WALL, Tags.DamageTypes.IS_PHYSICAL);
		tag(DamageTypes.GENERIC, Tags.DamageTypes.IS_PHYSICAL);

		tag(DamageTypes.GENERIC_KILL, Tags.DamageTypes.IS_TECHNICAL);
		tag(DamageTypes.OUTSIDE_BORDER, Tags.DamageTypes.IS_TECHNICAL);
		tag(DamageTypes.FELL_OUT_OF_WORLD, Tags.DamageTypes.IS_TECHNICAL);
		tag(Tags.DamageTypes.NO_FLINCH);
	}

	@Override
	protected FabricTagProvider<DamageType>.FabricTagBuilder tag(TagKey<DamageType> tag) {
		return super.getOrCreateTagBuilder(tag);
	}

	@SafeVarargs
	private void tag(ResourceKey<DamageType> type, TagKey<DamageType>... tags) {
		for (TagKey<DamageType> key : tags) {
			tag(key).add(type);
		}
	}
}
