package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import java.util.List;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Accessor("removalReason")
	void port_lib$setRemovalReason(Entity.RemovalReason removalReason);

	@Invoker("getEncodeId")
	String port_lib$getEntityString();

	@Invoker("collideWithShapes")
	static Vec3 port_lib$collideWithShapes(Vec3 vec3, AABB aABB, List<VoxelShape> list) {
		throw new AssertionError("Mixin application failed!");
	}
}
