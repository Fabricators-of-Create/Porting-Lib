package io.github.fabricators_of_create.porting_lib.extensions;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IShearable {
	default boolean isShearable(@NotNull ItemStack item, World world, BlockPos pos) {
		return false;
	}

	@NotNull
	default List<ItemStack> onSheared(@Nullable PlayerEntity player, @NotNull ItemStack item, World world, BlockPos pos, int fortune) {
		return Collections.emptyList();
	}
}
