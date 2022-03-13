package io.github.fabricators_of_create.porting_lib.entity;

import com.google.common.collect.ImmutableSet;

import net.fabricmc.fabric.impl.object.builder.FabricEntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;

public class ExtendedEntityType<T extends Entity> extends FabricEntityType<T> {
	public ExtendedEntityType(EntityFactory<T> factory, MobCategory spawnGroup, boolean bl, boolean summonable, boolean fireImmune, boolean spawnableFarFromPlayer, ImmutableSet<Block> spawnBlocks, EntityDimensions entityDimensions, int maxTrackDistance, int trackTickInterval, Boolean alwaysUpdateVelocity) {
		super(factory, spawnGroup, bl, summonable, fireImmune, spawnableFarFromPlayer, spawnBlocks, entityDimensions, maxTrackDistance, trackTickInterval, alwaysUpdateVelocity);
	}
}
