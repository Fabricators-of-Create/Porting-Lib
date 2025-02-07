package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.IShearable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(SnowGolemEntity.class)
public abstract class SnowGolemMixin implements IShearable {

	@Shadow
	public abstract boolean readyForShearing();

	@Shadow
	public abstract void setPumpkin(boolean pumpkinEquipped);

	@Unique
	@Override
	public boolean isShearable(@NotNull ItemStack item, World world, BlockPos pos) {
		return readyForShearing();
	}

	@NotNull
	@Override
	public List<ItemStack> onSheared(@Nullable PlayerEntity player, @NotNull ItemStack item, World world, BlockPos pos, int fortune) {
		world.playSoundFromEntity(null, (SnowGolemEntity) (Object) this, SoundEvents.ENTITY_SNOW_GOLEM_SHEAR, player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS, 1.0F, 1.0F);
		if (!world.isClient()) {
			setPumpkin(false);
			return Collections.singletonList(new ItemStack(Items.CARVED_PUMPKIN));
		}
		return Collections.emptyList();
	}
}
