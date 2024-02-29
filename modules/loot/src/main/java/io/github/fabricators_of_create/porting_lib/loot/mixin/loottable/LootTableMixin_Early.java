package io.github.fabricators_of_create.porting_lib.loot.mixin.loottable;

import java.util.function.Consumer;

import io.github.fabricators_of_create.porting_lib.loot.LootCollector;

import io.github.fabricators_of_create.porting_lib.loot.extensions.LootTableExtensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(value = LootTable.class, priority = 100)
public abstract class LootTableMixin_Early implements LootTableExtensions {
	@ModifyVariable(
			method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V",
			at = @At("HEAD"),
			argsOnly = true
	)
	private Consumer<ItemStack> wrapConsumer(Consumer<ItemStack> output) {
		// this needs to be done really early to get ahead of all uses of the consumer.
		// Lower integer priority is invoked first.
		LootCollector lootCollector = new LootCollector(output);
		// store the collector in a ThreadLocal for retrieval at tail.
		// This strategy is needed to handle the consumer being re-wrapped.
		// a Stack is used to handle recursion.
		this.port_lib$lootCollectorStack().get().push(lootCollector);
		return lootCollector;
	}
}
