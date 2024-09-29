package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.event.client.ColorHandlersCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.color.block.BlockColors;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {
  	@Inject(method = "createDefault", at = @At("TAIL"))
  	private static void registerModdedColorHandlers(CallbackInfoReturnable<BlockColors> cir, @Local BlockColors blockColors) {
    	ColorHandlersCallback.BLOCK.invoker().registerBlockColors(blockColors);
  	}
}
