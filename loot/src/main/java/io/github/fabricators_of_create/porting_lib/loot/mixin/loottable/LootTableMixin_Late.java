package io.github.fabricators_of_create.porting_lib.loot.mixin.loottable;

import java.util.function.Consumer;

import io.github.fabricators_of_create.porting_lib.loot.extensions.LootTableExtensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.loot.LootCollector;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(value = LootTable.class, priority = 50_000)
public class LootTableMixin_Late implements LootTableExtensions {
	@Inject(
			method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V",
			at = @At("RETURN")
	)
	private void finishCollectingLoot(LootContext context, Consumer<ItemStack> output, CallbackInfo ci) {
		// this needs to be done really late to catch all uses of the consumer before finishing.
		// Higher integer priority is invoked last.
		if (output instanceof LootCollector collector)
			collector.finish(this.getLootTableId(), context);
	}
}
