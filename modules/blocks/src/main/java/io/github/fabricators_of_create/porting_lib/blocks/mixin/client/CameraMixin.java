package io.github.fabricators_of_create.porting_lib.blocks.mixin.client;

import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.fabricators_of_create.porting_lib.blocks.injects.CameraInjection;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraInjection {
	@Shadow
	private float yRot;

	@Shadow
	private float xRot;

	@Shadow
	private boolean initialized;

	@Shadow
	private BlockGetter level;

	@Shadow
	@Final
	private BlockPos.MutableBlockPos blockPosition;

	@Shadow
	private Vec3 position;

	@Override
	public BlockState port_lib$getBlockAtCamera() {
		if (!this.initialized)
			return Blocks.AIR.defaultBlockState();
		else
			return this.level.getBlockState(this.blockPosition).port_lib$getStateAtViewpoint(this.level, this.blockPosition, this.position);
	}
}
