package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import io.github.fabricators_of_create.porting_lib.extensions.IShearable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SheepEntity.class)
public abstract class SheepMixin extends Entity implements IShearable {

	@Shadow
	@Final
	private static Map<DyeColor, ItemConvertible> ITEM_BY_DYE;

	public SheepMixin(EntityType<?> entityType, World level) {
		super(entityType, level);
	}

	@Shadow
	public abstract boolean readyForShearing();

	@Shadow
	public abstract void setSheared(boolean sheared);

	@Shadow
	public abstract DyeColor getColor();

	@Unique
	@Override
	public boolean isShearable(@NotNull ItemStack item, World world, BlockPos pos) {
		return readyForShearing();
	}

	@NotNull
	@Override
	public java.util.List<ItemStack> onSheared(@Nullable PlayerEntity player, @NotNull ItemStack item, World world, BlockPos pos, int fortune) {
		world.playSoundFromEntity(null, (SheepEntity) (Object) this, SoundEvents.ENTITY_SHEEP_SHEAR, player == null ? SoundCategory.BLOCKS : SoundCategory.PLAYERS, 1.0F, 1.0F);
		if (!world.isClient) {
			this.setSheared(true);
			int i = 1 + this.random.nextInt(3);

			java.util.List<ItemStack> items = new java.util.ArrayList<>();
			for (int j = 0; j < i; ++j) {
				items.add(new ItemStack(ITEM_BY_DYE.get(this.getColor())));
			}
			return items;
		}
		return java.util.Collections.emptyList();
	}
}
