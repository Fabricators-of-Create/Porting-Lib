package io.github.fabricators_of_create.porting_lib.blocks.mixin;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.VanillaCustomExpBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin implements VanillaCustomExpBlock {

	// Port Lib: Patch-in override for getExpDrop. Also fixes MC-273642 (Spawner XP drops bypass enchantments)
	// Original vanilla logic passes 15 + level.random.nextInt(15) + level.random.nextInt(15) to popExperience, bypassing enchantments
	@Override
	public int port_lib$getExpDrop(BlockState state, LevelAccessor level, BlockPos pos,
								   @Nullable BlockEntity blockEntity,
								   @Nullable Entity breaker, ItemStack tool) {
		return 15 + level.getRandom().nextInt(15) + level.getRandom().nextInt(15);
	}
}
