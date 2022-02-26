package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.math.Vector3f;

@Mixin(Vector3f.class)
public interface Vector3fAccessor {
	@Accessor("x")
	void port_lib$setX(float x);

	@Accessor("y")
	void port_lib$setY(float y);

	@Accessor("z")
	void port_lib$setZ(float z);
}
