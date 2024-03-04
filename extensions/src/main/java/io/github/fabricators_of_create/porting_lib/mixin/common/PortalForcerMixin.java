package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.ITeleporter;

import net.minecraft.world.level.portal.PortalForcer;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(PortalForcer.class)
public class PortalForcerMixin implements ITeleporter {
	// no need to do anything, all methods are defaulted.
}
