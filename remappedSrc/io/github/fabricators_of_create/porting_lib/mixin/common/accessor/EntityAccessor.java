package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Accessor("removalReason")
	void port_lib$setRemovalReason(Entity.RemovalReason removalReason);

	@Invoker("getEncodeId")
	String port_lib$getEntityString();

	@Invoker("collideWithShapes")
	static Vec3d port_lib$collideWithShapes(Vec3d vec3, Box aABB, List<VoxelShape> list) {
		throw new AssertionError("Mixin application failed!");
	}
}
