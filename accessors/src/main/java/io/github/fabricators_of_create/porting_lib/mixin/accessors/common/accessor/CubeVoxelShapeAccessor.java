package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

@Mixin(CubeVoxelShape.class)
public interface CubeVoxelShapeAccessor {
	@Invoker("<init>")
	static CubeVoxelShape port_lib$init(DiscreteVoxelShape discreteVoxelShape) {
		throw new AssertionError("Mixin application failed!");
	}
}
