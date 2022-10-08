package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import io.github.fabricators_of_create.porting_lib.attributes.extensions.EntityAttributes;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.phys.Vec3;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements EntityAttributes {
	@Shadow
	public float maxUpStep;

	@Inject(method = "collide", at = @At(value = "JUMP", opcode = Opcodes.IFGE))
	public void port_lib$modifyStepHeight(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
		this.maxUpStep = this.getStepHeight();
	}
}
