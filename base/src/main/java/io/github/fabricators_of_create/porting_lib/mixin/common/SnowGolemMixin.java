package io.github.fabricators_of_create.porting_lib.mixin.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.IShearable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

@Mixin(SnowGolem.class)
public abstract class SnowGolemMixin implements IShearable {

	@Shadow
	public abstract boolean readyForShearing();

	@Shadow
	public abstract void setPumpkin(boolean pumpkinEquipped);

	@Unique
	@Override
	public boolean isShearable(@Nonnull ItemStack item, Level world, BlockPos pos) {
		return readyForShearing();
	}

	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
		world.playSound(null, (SnowGolem) (Object) this, SoundEvents.SNOW_GOLEM_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
		if (!world.isClientSide()) {
			setPumpkin(false);
			return Collections.singletonList(new ItemStack(Items.CARVED_PUMPKIN));
		}
		return Collections.emptyList();
	}
}
