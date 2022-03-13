package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.client.ColorHandlersCallback;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.color.block.BlockColors;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
  @Inject(method = "createDefault", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
  private static void registerModdedColorHandlers(CallbackInfoReturnable<BlockColors> cir, BlockColors blockColors) {
    ColorHandlersCallback.BLOCK.invoker().registerBlockColors(blockColors);
  }
}
