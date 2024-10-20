package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.core.util.INBTSerializable;
import net.minecraft.core.HolderLookup;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.block.CustomRunningEffectsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Entity.class)
public abstract class EntityMixin implements INBTSerializable<CompoundTag> {
	@Shadow
	private Level level;
	@Shadow
	public abstract EntityType<?> getType();
	@Shadow
	@Nullable
	protected abstract String getEncodeId();
	@Shadow
	public abstract CompoundTag saveWithoutId(CompoundTag compoundTag);

	@Shadow
	public abstract void load(CompoundTag nbt);

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
		load(nbt);
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag ret = new CompoundTag();
		String id = getEncodeId();
		if (id != null) {
			ret.putString("id", getEncodeId());
		}
		return saveWithoutId(ret);
	}

	// RUNNING EFFECTS

	@Definition(id = "blockState", local = @Local(type = BlockState.class))
	@Definition(id = "getRenderShape", method = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;")
	@Definition(id = "INVISIBLE", field = "Lnet/minecraft/world/level/block/RenderShape;INVISIBLE:Lnet/minecraft/world/level/block/RenderShape;")
	@Expression("blockState.getRenderShape() != INVISIBLE")
	@ModifyExpressionValue(method = "spawnSprintParticle", at = @At("MIXINEXTRAS:EXPRESSION"))
	public boolean port_lib$spawnSprintParticle(boolean original, @Local BlockPos pos, @Local BlockState state) {
		//noinspection ConstantValue
		return original && !(state.getBlock() instanceof CustomRunningEffectsBlock custom &&
				custom.addRunningEffects(state, level, pos, (Entity) (Object) this));
	}

}
