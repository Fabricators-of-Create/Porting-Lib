package io.github.fabricators_of_create.porting_lib.loot.mixin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.loot.extensions.LootPoolBuilderExtension;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootPoolExtensions;
import net.minecraft.world.level.storage.loot.LootPool;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;

@Mixin(LootPool.class)
public class LootPoolMixin implements LootPoolExtensions {
	@Unique
	private String name;

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Mixin(LootPool.Builder.class)
	public static class LootPoolBuilderMixin implements LootPoolBuilderExtension {
		@Unique
		private String name;

		@Override
		public LootPool.Builder name(String name) {
			this.name = name;
			//noinspection DataFlowIssue
			return (LootPool.Builder) (Object) this;
		}

		@ModifyReturnValue(method = "build", at = @At("RETURN"))
		public LootPool setName(LootPool pool) {
			pool.setName(name);
			return pool;
		}
	}

	@Mixin(LootPool.Serializer.class)
	public static class LootPoolSerializerMixin {
		@Inject(
				method = "serialize(Lnet/minecraft/world/level/storage/loot/LootPool;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;",
				at = @At(
						value = "INVOKE",
						target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V",
						ordinal = 0
				),
				locals = LocalCapture.CAPTURE_FAILHARD
		)
		private void serializeName(LootPool pool, Type type, JsonSerializationContext ctx, CallbackInfoReturnable<JsonElement> cir, JsonObject json) {
			String name = pool.getName();
			if (name != null && !name.startsWith("custom#"))
				json.addProperty("name", name);
		}

		@ModifyReturnValue(
				method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/world/level/storage/loot/LootPool;",
				at = @At("RETURN")
		)
		private LootPool deserializeName(LootPool pool, JsonElement element, Type type, JsonDeserializationContext ctx) {
			JsonElement name = element.getAsJsonObject().get("name");
			if (name instanceof JsonPrimitive primitive && primitive.isString())
				pool.setName(name.getAsString());
			return pool;
		}
	}

}
