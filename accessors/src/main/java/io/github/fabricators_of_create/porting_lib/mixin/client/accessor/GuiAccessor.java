package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.Gui;

import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public interface GuiAccessor {
	@Accessor("screenWidth")
	int porting_lib$screenWidth();

	@Accessor("screenHeight")
	int porting_lib$screenHeight();

	@Invoker("canRenderCrosshairForSpectator")
	boolean porting_lib$canRenderCrosshairForSpectator(HitResult hitResult);
}
