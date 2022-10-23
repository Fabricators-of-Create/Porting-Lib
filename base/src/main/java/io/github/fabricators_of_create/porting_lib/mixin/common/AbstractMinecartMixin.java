package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.MinecartEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.block.MinecartPassHandlerBlock;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.AbstractMinecartExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.INBTSerializable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity implements AbstractMinecartExtensions, INBTSerializable<CompoundTag> {
	private AbstractMinecartMixin(EntityType<?> entityType, Level world) {
		super(entityType, world);
	}

	@Shadow
	protected abstract double getMaxSpeed();

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
	public void port_lib$abstractMinecartEntity(EntityType<?> entityType, Level world, CallbackInfo ci) {
		MinecartEvents.SPAWN.invoker().minecartSpawn((AbstractMinecart) (Object) this, world);
	}

	@Inject(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I", ordinal = 4))
	protected void port_lib$moveAlongTrack(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (blockState.getBlock() instanceof MinecartPassHandlerBlock handler) {
			handler.onMinecartPass(blockState, level, blockPos, (AbstractMinecart) (Object) this);
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void port_lib$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		MinecartEvents.WRITE.invoker().minecartWrite((AbstractMinecart) (Object) this, compound);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void port_lib$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		MinecartEvents.READ.invoker().minecartRead((AbstractMinecart) (Object) this, compound);
	}

	@Override
	public void moveMinecartOnRail(BlockPos pos) {
		double d24 = isVehicle() ? 0.75D : 1.0D;
		double d25 = getMaxSpeed(); // getMaxSpeed instead of getMaxSpeedWithRail *should* be fine after intense pain looking at Forge patches
		Vec3 vec3d1 = getDeltaMovement();
		move(MoverType.SELF, new Vec3(Mth.clamp(d24 * vec3d1.x, -d25, d25), 0.0D, Mth.clamp(d24 * vec3d1.z, -d25, d25)));
	}

	@Override
	public BlockPos getCurrentRailPos() {
		BlockPos pos = new BlockPos(Mth.floor(getX()), Mth.floor(getY()), Mth.floor(getZ()));
		BlockPos below = pos.below();
		if (level.getBlockState(below).is(BlockTags.RAILS)) {
			pos = below;
		}

		return pos;
	}
}
