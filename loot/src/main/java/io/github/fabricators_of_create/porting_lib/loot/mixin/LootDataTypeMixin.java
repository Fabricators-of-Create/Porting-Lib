package io.github.fabricators_of_create.porting_lib.loot.mixin;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(LootDataType.class)
public class LootDataTypeMixin {
	@Inject(method = "deserialize", at = @At("RETURN"))
	private <T> void setLootTableId(ResourceLocation id, JsonElement json, @NotNull CallbackInfoReturnable<Optional<T>> cir) {
		Optional<T> data = cir.getReturnValue();
		if (data.isPresent() && data.get() instanceof LootTable lootTable)
			lootTable.setLootTableId(id);
	}
}
