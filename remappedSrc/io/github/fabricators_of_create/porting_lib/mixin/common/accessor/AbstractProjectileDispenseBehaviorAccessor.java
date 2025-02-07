package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ProjectileDispenserBehavior.class)
public interface AbstractProjectileDispenseBehaviorAccessor {
	@Invoker("getProjectile")
	ProjectileEntity port_lib$getProjectile(World level, Position position, ItemStack stack);

	@Invoker("getUncertainty")
	float port_lib$getUncertainty();

	@Invoker("getPower")
	float port_lib$getPower();
}
