package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

@Mixin(MushroomCow.class)
public abstract class MushroomCowMixin implements IShearable {

	@Shadow
	public abstract boolean readyForShearing();

	@Shadow
	public abstract void shear(SoundSource category);

	@Shadow
	public abstract MushroomCow.MushroomType getMushroomType();

	@Unique
	@Override
	public boolean isShearable(@NotNull ItemStack item, Level world, BlockPos pos) {
		return readyForShearing();
	}

	@Override
	@NotNull
	public List<ItemStack> onSheared(@Nullable Player player, @NotNull ItemStack item, Level world, BlockPos pos, int fortune) {
		shear(player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS);
		List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < 5; ++i) {
			items.add(new ItemStack(this.getMushroomType().getBlockState().getBlock()));
		}
		return items;
	}
}
