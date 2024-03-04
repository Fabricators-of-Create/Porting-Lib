package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.ITeleporter;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.portal.PortalForcer;

@Mixin(PortalForcer.class)
public class PortalForcerMixin implements ITeleporter {
	// no need to do anything, all methods are defaulted.
}
