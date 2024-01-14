package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.extensions.AbstractMinecartExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity implements AbstractMinecartExtensions {
	@Shadow
	protected abstract double getMaxSpeed();

	public AbstractMinecartMixin(EntityType<?> variant, Level world) {
		super(variant, world);
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
		if (level().getBlockState(below).is(BlockTags.RAILS)) {
			pos = below;
		}

		return pos;
	}
}
