package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import io.github.fabricators_of_create.porting_lib.blocks.BlockHooks;
import io.github.fabricators_of_create.porting_lib.blocks.injects.AABBInjection;
import net.minecraft.world.phys.AABB;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AABB.class)
public class AABBMixin implements AABBInjection {
	@Shadow
	@Final
	public double minX;

	@Shadow
	@Final
	public double minY;

	@Shadow
	@Final
	public double minZ;

	@Shadow
	@Final
	public double maxX;

	@Shadow
	@Final
	public double maxY;

	@Shadow
	@Final
	public double maxZ;

	@Override
	public boolean port_lib$isInfinite() {
		return (Object) this == BlockHooks.INFINITE_AABB || (Double.isInfinite(this.minX) && Double.isInfinite(this.minY) && Double.isInfinite(this.minZ)
				&& Double.isInfinite(this.maxX) && Double.isInfinite(this.maxY) && Double.isInfinite(this.maxZ));
	}
}
