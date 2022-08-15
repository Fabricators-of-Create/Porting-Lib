package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.data.ConditionalAdvancement;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.DeserializationContext;

@Mixin(Advancement.Builder.class)
public class AdvancementBuilderMixin {
	@Inject(method = "fromJson", at = @At("HEAD"), cancellable = true)
	private static void port_lib$conditional(JsonObject json, DeserializationContext context, CallbackInfoReturnable<Advancement.Builder> cir) {
		if (ConditionalAdvancement.processConditional(json) == null) cir.setReturnValue(null);
	}

	@ModifyVariable(method = "fromJson", at = @At("HEAD"), argsOnly = true)
	private static JsonObject port_lib$modifyJson(JsonObject jsonObject) {
		return ConditionalAdvancement.processConditional(jsonObject);
	}
}
