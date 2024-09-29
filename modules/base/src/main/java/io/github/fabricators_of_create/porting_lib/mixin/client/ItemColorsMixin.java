package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

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
  @Inject(method = "createDefault", at = @At("TAIL"))
  private static void registerModdedColorHandlers(BlockColors colors, CallbackInfoReturnable<ItemColors> cir, @Local ItemColors itemcolors) {
    ColorHandlersCallback.ITEM.invoker().registerItemColors(itemcolors, colors);
  }
}
