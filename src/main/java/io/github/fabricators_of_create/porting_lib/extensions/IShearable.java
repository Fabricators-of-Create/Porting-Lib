package io.github.fabricators_of_create.porting_lib.extensions;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IShearable {
	default boolean isShearable(@Nonnull ItemStack item, Level world, BlockPos pos) {
		return false;
	}

	@Nonnull
	default List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
		return Collections.emptyList();
	}
}
