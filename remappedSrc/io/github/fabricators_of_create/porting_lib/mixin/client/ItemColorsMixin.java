package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.client.ColorHandlersCallback;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemColors.class)
public abstract class ItemColorsMixin {
  @Inject(method = "createDefault", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
  private static void registerModdedColorHandlers(BlockColors colors, CallbackInfoReturnable<ItemColors> cir, ItemColors itemcolors) {
    ColorHandlersCallback.ITEM.invoker().registerItemColors(itemcolors, colors);
  }
}
