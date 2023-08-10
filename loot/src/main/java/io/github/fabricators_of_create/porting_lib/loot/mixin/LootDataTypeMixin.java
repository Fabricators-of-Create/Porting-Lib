package io.github.fabricators_of_create.porting_lib.loot.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataType;

import net.minecraft.world.level.storage.loot.LootTable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LootDataType.class)
public class LootDataTypeMixin {
	@Inject(method = "method_51205", at = @At("RETURN"))
	private static <T> void setLootTableId(Gson gson, Class<T> class_, String string, ResourceLocation id, JsonElement json, CallbackInfoReturnable<Optional<T>> cir) {
		Optional<T> data = cir.getReturnValue();
		if (data.isPresent() && data.get() instanceof LootTable lootTable)
			lootTable.setLootTableId(id);
	}
}
