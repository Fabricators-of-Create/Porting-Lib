package io.github.fabricators_of_create.porting_lib.blocks.impl.mixin;

import io.github.fabricators_of_create.porting_lib.blocks.api.addons.CustomFrictionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends Entity {
	public ExperienceOrbMixin(EntityType<?> variant, Level world) {
		super(variant, world);
	}

	@ModifyVariable(
			method = "tick",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	private float port_lib$setFriction(float original) {
		BlockPos pos = BlockPos.containing(getX(), getY() - 1, getZ());
		BlockState state = level().getBlockState(pos);
		if (state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, level(), pos, this);
		}
		return original;
	}
}
