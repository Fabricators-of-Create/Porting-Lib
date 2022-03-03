package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import io.github.fabricators_of_create.porting_lib.util.ToolActions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import slimeknights.mantle.lib.util.ToolAction;
import slimeknights.mantle.lib.util.ToolActions;

import java.util.Optional;

public interface BlockExtensions {
	default boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
		return false;
	}

	default boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	default boolean addDestroyEffects(BlockState state, Level world, BlockPos pos, ParticleEngine manager) {
		return false;
	}

	default boolean isFlammable(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return state.getFlammability(world, pos, face) > 0;
	}

	default int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return ((FireBlockExtensions) Blocks.FIRE).invokeGetBurnOdd(state);
	}

	default boolean isBurning(BlockState state, BlockGetter world, BlockPos pos) {
		return this == Blocks.FIRE || this == Blocks.LAVA;
	}

	default SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
		return ((Block) this).getSoundType(state);
	}

	default int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		return state.getLightEmission();
	}

	default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
		return state.getBlock() instanceof HalfTransparentBlock || state.getBlock() instanceof LeavesBlock;
	}

	default void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {}

	default float getSlipperiness(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
		return ((Block) this).getFriction();
	}

	default boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		((Block) this).playerWillDestroy(world, pos, state, player);
		return world.setBlock(pos, fluid.createLegacyBlock(), world.isClientSide ? 11 : 3);
	}

	default public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
		return player.hasCorrectToolForDrops(state);
	}

	@Nullable
	default BlockState getToolModifiedState(BlockState state, Level world, BlockPos pos, Player player, ItemStack stack, ToolAction toolAction) {
		if (!stack.canPerformAction(toolAction)) return null;
//    if (ToolActions.AXE_STRIP.equals(toolAction)) return AxeItem.getAxeStrippingState(state);
		else if(ToolActions.AXE_SCRAPE.equals(toolAction)) return WeatheringCopper.getPrevious(state).orElse(null);
		else if(ToolActions.AXE_WAX_OFF.equals(toolAction)) return Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map((p_150694_) -> {
			return p_150694_.withPropertiesOf(state);
		}).orElse(null);
		//else if(ToolActions.HOE_TILL.equals(toolAction)) return HoeItem.getHoeTillingState(state); //TODO HoeItem bork
//    else if (ToolActions.SHOVEL_FLATTEN.equals(toolAction)) return ShovelItem.getShovelPathingState(state);
		return null;
	}

	@Nullable
	default BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
		return state.getBlock() == Blocks.LAVA ? BlockPathTypes.LAVA : state.isBurning(world, pos) ? BlockPathTypes.DAMAGE_FIRE : null;
	}
}
