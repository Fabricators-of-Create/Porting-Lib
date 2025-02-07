package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LootManager.class)
public class LootTablesMixin {
	@Inject(method = {"m_upgvhpqp", "method_20711", "lambda$apply$0"}, at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void port_lib$setName(ImmutableMap.Builder builder, Identifier id, JsonElement json, CallbackInfo ci, LootTable lootTable) {
		lootTable.setLootTableId(id);
	}
}
