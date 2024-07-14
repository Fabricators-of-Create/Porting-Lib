package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixerBuilder;

import com.mojang.datafixers.schemas.Schema;

import io.github.fabricators_of_create.porting_lib.fixes.ForgeBlockEntityFix;
import net.minecraft.util.datafix.DataFixers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
public class DataFixersMixin {
	@Inject(method = "addFixers", at = @At("TAIL"))
	private static void addBlockEntityDataFix(DataFixerBuilder dataFixerBuilder, CallbackInfo ci, @Local(index = 234) Schema lastSchema) {
		dataFixerBuilder.addFixer(new ForgeBlockEntityFix(lastSchema, false));
	}
}
