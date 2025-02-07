package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.extensions.CameraExtensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExtensions {
	@Shadow
	private float yRot;

	@Shadow
	private float xRot;

	@Shadow
	private boolean initialized;

	@Shadow
	private BlockView level;

	@Shadow
	@Final
	private BlockPos.Mutable blockPosition;

	@Unique
	@Override
	public void setAnglesInternal(float yaw, float pitch) {
		this.yRot = yaw;
		this.xRot = pitch;
	}

	@Unique
	@Override
	public BlockState getBlockAtCamera() {
		if (!this.initialized)
			return Blocks.AIR.getDefaultState();
		else
			return this.level.getBlockState(this.blockPosition);
	}
}
