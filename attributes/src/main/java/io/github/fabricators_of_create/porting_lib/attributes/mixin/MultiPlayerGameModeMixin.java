package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyReturnValue(method = "getPickRange", at = @At("RETURN"))
	private float modifyPickRange(float original) {
//		if (original != 5.0F && original != 4.5F) // If the original doesn't equal one of these two another mod has modified it
//			return original;
		return (float) this.minecraft.player.getBlockReach();
	}
}
