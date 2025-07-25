package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CollisionExtendsVerticallyBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomRunningEffectsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
	// RUNNING EFFECTS

	@Shadow
	public abstract Level level();

	@Definition(id = "blockState", local = @Local(type = BlockState.class))
	@Definition(id = "getRenderShape", method = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;")
	@Definition(id = "INVISIBLE", field = "Lnet/minecraft/world/level/block/RenderShape;INVISIBLE:Lnet/minecraft/world/level/block/RenderShape;")
	@Expression("blockState.getRenderShape() != INVISIBLE")
	@ModifyExpressionValue(method = "spawnSprintParticle", at = @At("MIXINEXTRAS:EXPRESSION"))
	public boolean port_lib$spawnSprintParticle(boolean original, @Local BlockPos pos, @Local BlockState state) {
		//noinspection ConstantValue
		return original && !(state.getBlock() instanceof CustomRunningEffectsBlock custom &&
				custom.addRunningEffects(state, this.level(), pos, (Entity) (Object) this));
	}

	@WrapOperation(method = "getOnPos(F)Lnet/minecraft/core/BlockPos;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
	private boolean port_lib$tryCheckCollisionsExtendVertically(BlockState instance, TagKey tagKey, Operation<Boolean> original, @Local BlockPos pos) {
		if (instance.getBlock() instanceof CollisionExtendsVerticallyBlock collisionExtendsVerticallyBlock) {
			return collisionExtendsVerticallyBlock.collisionExtendsVertically(instance, this.level(), pos, (Entity) (Object) this);
		}

		return original.call(instance, tagKey);
	}
}
