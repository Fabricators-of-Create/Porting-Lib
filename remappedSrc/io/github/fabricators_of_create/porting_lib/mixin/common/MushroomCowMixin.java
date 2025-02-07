package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(MooshroomEntity.class)
public abstract class MushroomCowMixin implements IShearable {

	@Shadow
	public abstract boolean readyForShearing();

	@Shadow
	public abstract void shear(SoundCategory category);

	@Shadow
	public abstract MooshroomEntity.Type getMushroomType();

	@Unique
	@Override
	public boolean isShearable(@NotNull ItemStack item, World world, BlockPos pos) {
		return readyForShearing();
	}

	@Override
	@NotNull
	public List<ItemStack> onSheared(@Nullable PlayerEntity player, @NotNull ItemStack item, World world, BlockPos pos, int fortune) {
		shear(player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS);
		List<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < 5; ++i) {
			items.add(new ItemStack(this.getMushroomType().getMushroomState().getBlock()));
		}
		return items;
	}
}
