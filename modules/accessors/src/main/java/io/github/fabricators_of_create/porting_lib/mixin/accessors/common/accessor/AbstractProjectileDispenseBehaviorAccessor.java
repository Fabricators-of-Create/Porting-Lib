package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractProjectileDispenseBehavior.class)
public interface AbstractProjectileDispenseBehaviorAccessor {
	@Invoker("getProjectile")
	Projectile port_lib$getProjectile(Level level, Position position, ItemStack stack);

	@Invoker("getUncertainty")
	float port_lib$getUncertainty();

	@Invoker("getPower")
	float port_lib$getPower();
}
