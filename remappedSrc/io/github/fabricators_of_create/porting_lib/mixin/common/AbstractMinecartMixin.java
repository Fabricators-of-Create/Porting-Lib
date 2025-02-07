package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.MinecartEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.block.MinecartPassHandlerBlock;
import io.github.fabricators_of_create.porting_lib.extensions.AbstractMinecartExtensions;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartMixin extends Entity implements AbstractMinecartExtensions, INBTSerializable<NbtCompound> {
	private AbstractMinecartMixin(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Shadow
	protected abstract double getMaxSpeed();

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
	public void port_lib$abstractMinecartEntity(EntityType<?> entityType, World world, CallbackInfo ci) {
		MinecartEvents.SPAWN.invoker().minecartSpawn((AbstractMinecartEntity) (Object) this, world);
	}

	@Inject(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I", ordinal = 4))
	protected void port_lib$moveAlongTrack(BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
		if (blockState.getBlock() instanceof MinecartPassHandlerBlock handler) {
			handler.onMinecartPass(blockState, world, blockPos, (AbstractMinecartEntity) (Object) this);
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void port_lib$addAdditionalSaveData(NbtCompound compound, CallbackInfo ci) {
		MinecartEvents.WRITE.invoker().minecartWrite((AbstractMinecartEntity) (Object) this, compound);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void port_lib$readAdditionalSaveData(NbtCompound compound, CallbackInfo ci) {
		MinecartEvents.READ.invoker().minecartRead((AbstractMinecartEntity) (Object) this, compound);
	}

	@Override
	public void moveMinecartOnRail(BlockPos pos) {
		double d24 = hasPassengers() ? 0.75D : 1.0D;
		double d25 = getMaxSpeed(); // getMaxSpeed instead of getMaxSpeedWithRail *should* be fine after intense pain looking at Forge patches
		Vec3d vec3d1 = getVelocity();
		move(MovementType.SELF, new Vec3d(MathHelper.clamp(d24 * vec3d1.x, -d25, d25), 0.0D, MathHelper.clamp(d24 * vec3d1.z, -d25, d25)));
	}

	@Override
	public BlockPos getCurrentRailPos() {
		BlockPos pos = new BlockPos(MathHelper.floor(getX()), MathHelper.floor(getY()), MathHelper.floor(getZ()));
		BlockPos below = pos.down();
		if (world.getBlockState(below).isIn(BlockTags.RAILS)) {
			pos = below;
		}

		return pos;
	}
}
