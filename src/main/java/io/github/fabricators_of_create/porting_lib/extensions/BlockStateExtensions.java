package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import javax.annotation.Nullable;

public interface BlockStateExtensions {

  private BlockState self() {
    return (BlockState)this;
  }

	default boolean addRunningEffects(Level world, BlockPos pos, Entity entity) {
		return ((BlockState) this).getBlock().addRunningEffects((BlockState) this, world, pos, entity);
	}

	default boolean addLandingEffects(ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return ((BlockState) this).getBlock().addLandingEffects((BlockState) this, worldserver, pos, state2, entity, numberOfParticles);
	}

	@Environment(EnvType.CLIENT)
	default boolean addDestroyEffects(Level world, BlockPos pos, ParticleEngine manager) {
		return ((BlockState) this).getBlock().addDestroyEffects((BlockState) this, world, pos, manager);
	}

	default boolean isFlammable(BlockGetter world, BlockPos pos, Direction face) {
		return ((BlockState) this).getBlock().isFlammable((BlockState) this, world, pos, face);
	}

	default int getFlammability(BlockGetter world, BlockPos pos, Direction face) {
		return ((BlockState) this).getBlock().getFlammability((BlockState) this, world, pos, face);
	}

	default void onNeighborChange(LevelReader world, BlockPos pos, BlockPos neighbor) {
    ((BlockState) this).getBlock().onNeighborChange((BlockState) this, world, pos, neighbor);
	}

  default boolean onDestroyedByPlayer(Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
    return ((BlockState) this).getBlock().onDestroyedByPlayer((BlockState) this, world, pos, player, willHarvest, fluid);
  }

	default float getSlipperiness(LevelReader world, BlockPos pos, @Nullable Entity entity)
	{
		return ((BlockState) this).getBlock().getSlipperiness((BlockState) this, world, pos, entity);
	}

  default boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player)
  {
    return ((BlockState) this).getBlock().canHarvestBlock(((BlockState) this), world, pos, player);
  }

  default boolean isBurning(BlockGetter world, BlockPos pos)
  {
    return ((BlockState) this).getBlock().isBurning((BlockState) this, world, pos);
  }

  @Nullable
  default BlockState getToolModifiedState(Level world, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction) {
    return self().getBlock().getToolModifiedState(self(), world, pos, player, stack, toolAction);
  }
}
