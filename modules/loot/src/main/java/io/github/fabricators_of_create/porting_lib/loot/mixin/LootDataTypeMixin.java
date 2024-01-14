package io.github.fabricators_of_create.porting_lib.loot.mixin;

import com.google.gson.JsonElement;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataType;

import net.minecraft.world.level.storage.loot.LootTable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(LootDataType.class)
public class LootDataTypeMixin {
	@ModifyReturnValue(method = "deserialize", at = @At("RETURN"))
	private static <T> Optional<T> setLootTableId(Optional<T> optional, ResourceLocation id, JsonElement json) {
		if (optional.isPresent() && optional.get() instanceof LootTable lootTable) {
			lootTable.setLootTableId(id);
		}
		return optional;
	}
}
