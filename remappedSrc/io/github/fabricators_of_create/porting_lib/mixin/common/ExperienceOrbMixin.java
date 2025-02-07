package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.util.XpRepairItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.block.CustomFrictionBlock;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbMixin extends Entity {
	@Shadow
	private int value;

	@Unique
	private ItemStack port_lib$stackToMend = null;

	public ExperienceOrbMixin(EntityType<?> entityType, World level) {
		super(entityType, level);
	}

	@ModifyVariable(
			method = "tick",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	private float port_lib$setFriction(float original) {
		BlockPos pos = new BlockPos(getX(), getY() - 1, getZ());
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, world, pos, this);
		}
		return original;
	}

	@ModifyExpressionValue(
			method = "repairPlayerItems",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;",
					remap = false
			)
	)
	private Object port_lib$grabStackToMend(Object value) {
		if (value instanceof ItemStack stack) {
			this.port_lib$stackToMend = stack;
		}
		return value;
	}

	@ModifyExpressionValue(
			method = "repairPlayerItems",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/ExperienceOrb;xpToDurability(I)I"
			)
	)
	private int port_lib$modifyRepairAmount(int durability) {
		ItemStack stack = port_lib$stackToMend;
		// set to null after we've used it
		port_lib$stackToMend = null;
		if (stack != null && stack.getItem() instanceof XpRepairItem custom) {
			float ratio = custom.getXpRepairRatio(stack);
			return (int) ratio * this.value;
		}
		return durability;
	}
}
