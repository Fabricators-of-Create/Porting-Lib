package io.github.fabricators_of_create.porting_lib.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public interface CustomHitEffectsBlock {
	/**
	 * Custom effects when your block is hit by a player.
	 * @return true to cancel vanilla effects
	 */
	@Environment(EnvType.CLIENT)
	boolean applyCustomHitEffects(BlockState state, Level level, HitResult target, ParticleEngine engine);
}
