package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import com.mojang.serialization.JsonOps;

import io.github.fabricators_of_create.porting_lib.util.ItemPredicateRegistry;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.ItemPredicate;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(ItemPredicate.class)
public abstract class ItemPredicateMixin {
	@Inject(method = "fromJson", at = @At("HEAD"), cancellable = true)
	private static void port_lib$customItemPredicates(@Nullable JsonElement json, CallbackInfoReturnable<Optional<ItemPredicate>> cir) {
		if (json == null) return;
		JsonObject obj = json.getAsJsonObject();
		if (obj.has("type")) {
			final ResourceLocation rl = new ResourceLocation(GsonHelper.getAsString(obj, "type"));
			if (ItemPredicateRegistry.custom_predicates.containsKey(rl))
				cir.setReturnValue(Optional.of(Util.getOrThrow(ItemPredicateRegistry.custom_predicates.get(rl).parse(JsonOps.INSTANCE, json), JsonParseException::new)));
			else throw new JsonSyntaxException("There is no ItemPredicate of type " + rl);
		}
	}
}
