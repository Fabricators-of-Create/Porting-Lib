package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import io.github.fabricators_of_create.porting_lib.extensions.LootPoolBuilderExtension;
import io.github.fabricators_of_create.porting_lib.extensions.LootPoolExtensions;
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
		private String name;

		@Override
		public LootPool.Builder name(String name) {
			this.name = name;
			return (LootPool.Builder) (Object) this;
		}

		@Inject(
				method = "build",
				at = @At("RETURN"),
				locals = LocalCapture.CAPTURE_FAILHARD
		)
		public void port_lib$customName(CallbackInfoReturnable<LootPool> cir) {
			cir.getReturnValue().setName(name);
		}
	}

	@Mixin(LootPool.Serializer.class)
	public static class LootPoolSerializerMixin {
		@Inject(
				method = "serialize(Lnet/minecraft/world/level/storage/loot/LootPool;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;",
				at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;add(Ljava/lang/String;Lcom/google/gson/JsonElement;)V", ordinal = 0),
				locals = LocalCapture.CAPTURE_FAILHARD
		)
		public void port_lib$customName(LootPool lootPool, Type type, JsonSerializationContext jsonSerializationContext, CallbackInfoReturnable<JsonElement> cir, JsonObject jsonObject) {
			if (lootPool.getName() != null && !lootPool.getName().startsWith("custom#"))
				jsonObject.add("name", jsonSerializationContext.serialize(lootPool.getName()));
		}
	}

}
