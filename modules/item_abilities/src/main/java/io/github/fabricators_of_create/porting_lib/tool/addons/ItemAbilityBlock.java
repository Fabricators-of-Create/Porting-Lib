package io.github.fabricators_of_create.porting_lib.tool.addons;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

import java.util.Optional;

public interface ItemAbilityBlock {
	/**
	 * Returns the state that this block should transform into when right-clicked by a tool.
	 * For example: Used to determine if {@link ItemAbilities#AXE_STRIP an axe can strip},
	 * {@link ItemAbilities#SHOVEL_FLATTEN a shovel can path}, or {@link ItemAbilities#HOE_TILL a hoe can till}.
	 * Returns {@code null} if nothing should happen.
	 *
	 * @param state       The current state
	 * @param context     The use on context that the action was performed in
	 * @param itemAbility The action being performed by the tool
	 * @param simulate    If {@code true}, no actions that modify the world in any way should be performed. If {@code false}, the world may be modified.
	 * @return The resulting state after the action has been performed
	 */
	@Nullable
	default BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		ItemStack itemStack = context.getItemInHand();
		if (!itemStack.canPerformAction(itemAbility))
			return null;

		if (ItemAbilities.AXE_STRIP == itemAbility) {
			Block block = AxeItem.STRIPPABLES.get(state.getBlock());
			return block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)) : null;
		} else if (ItemAbilities.AXE_SCRAPE == itemAbility) {
			return WeatheringCopper.getPrevious(state).orElse(null);
		} else if (ItemAbilities.AXE_WAX_OFF == itemAbility) {
			Block waxOffBlock = HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock());
			return Optional.ofNullable(waxOffBlock).map(block -> block.withPropertiesOf(state)).orElse(null);
		} else if (ItemAbilities.SHOVEL_FLATTEN == itemAbility) {
			return ShovelItem.FLATTENABLES.get(state.getBlock());
		} else if (ItemAbilities.HOE_TILL == itemAbility) {
			// Logic copied from HoeItem#TILLABLES; needs to be kept in sync during updating
			Block block = state.getBlock();
			if (block == Blocks.ROOTED_DIRT) {
				if (!simulate && !context.getLevel().isClientSide) {
					Block.popResourceFromFace(context.getLevel(), context.getClickedPos(), context.getClickedFace(), new ItemStack(Items.HANGING_ROOTS));
				}
				return Blocks.DIRT.defaultBlockState();
			} else if ((block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH || block == Blocks.DIRT || block == Blocks.COARSE_DIRT) &&
					context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
				return block == Blocks.COARSE_DIRT ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
			}
		} else if (ItemAbilities.SHEARS_TRIM == itemAbility) {
			if (state.getBlock() instanceof GrowingPlantHeadBlock growingPlant && !growingPlant.isMaxAge(state)) {
				if (!simulate)
					context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
				return growingPlant.getMaxAgeState(state);
			}
		} else if (ItemAbilities.SHOVEL_DOUSE == itemAbility) {
			if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
				if (!simulate) {
					CampfireBlock.dowse(context.getPlayer(), context.getLevel(), context.getClickedPos(), state);
				}
				return state.setValue(CampfireBlock.LIT, false);
			}
		} else if (ItemAbilities.FIRESTARTER_LIGHT == itemAbility) {
			if (CampfireBlock.canLight(state) || CandleBlock.canLight(state) || CandleCakeBlock.canLight(state)) {
				return state.setValue(BlockStateProperties.LIT, false);
			}
		}

		return null;
	}
}
