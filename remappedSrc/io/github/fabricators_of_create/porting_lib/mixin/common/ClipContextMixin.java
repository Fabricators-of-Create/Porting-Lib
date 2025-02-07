package io.github.fabricators_of_create.porting_lib.mixin.common;

import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RaycastContext.class)
public abstract class ClipContextMixin {
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/CollisionContext;of(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/shapes/CollisionContext;"))
	private ShapeContext port_lib$redirectCollisionContext(Entity entity) {
		if (entity == null) {
			return ShapeContext.absent();
		}
		return ShapeContext.of(entity);
	}
}
