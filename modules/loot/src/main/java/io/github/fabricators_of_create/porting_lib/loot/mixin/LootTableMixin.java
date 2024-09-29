package io.github.fabricators_of_create.porting_lib.loot.mixin;

import java.util.Objects;
import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import io.github.fabricators_of_create.porting_lib.loot.LootCollector;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootTableBuilderExtensions;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootTableExtensions;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.storage.loot.LootContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(value = LootTable.class, priority = 50_000)
public class LootTableMixin implements LootTableExtensions {
	@Unique
	private ResourceLocation lootTableId;

	@Override
	public void setLootTableId(final ResourceLocation id) {
		if (this.lootTableId != null)
			throw new IllegalStateException("Attempted to rename loot table from '" + this.lootTableId + "' to '" + id + "': this is not supported");
		this.lootTableId = Objects.requireNonNull(id);
	}

	@Override
	public ResourceLocation getLootTableId() {
		return this.lootTableId;
	}

	@WrapMethod(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V")
	private void finishCollectingLoot(LootContext lootContext, Consumer<ItemStack> consumer, Operation<Void> original) {
		LootCollector lootCollector = new LootCollector(consumer);
		original.call(lootContext, lootCollector);
		lootCollector.finish(this.getLootTableId(), lootContext);
	}

	@Mixin(LootTable.Builder.class)
	public static class BuilderMixin implements LootTableBuilderExtensions {
		@Unique
		private ResourceLocation id;

		@Override
		public void port_lib$setId(ResourceLocation id) {
			this.id = id;
		}

		@ModifyReturnValue(method = "build", at = @At("RETURN"))
		private LootTable addId(LootTable table) {
			if (this.id != null)
				table.setLootTableId(this.id);
			return table;
		}
	}
}
