package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.List;
import java.util.Objects;

import io.github.fabricators_of_create.porting_lib.extensions.PortingLibLootTableBuilder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.extensions.LootTableExtensions;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootTable.class)
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

	@ModifyReturnValue(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Ljava/util/List;", at = @At("RETURN"))
	private List<ItemStack> applyGlobalModifiers(List<ItemStack> list, LootContext context) {
		return PortingHooks.modifyLoot(getLootTableId(), list, context);
	}

	@Mixin(LootTable.Builder.class)
	public static class BuilderMixin implements PortingLibLootTableBuilder {
		private ResourceLocation port_lib$lootTableId;

		@Override
		public ResourceLocation getPortingLibLootTableId() {
			return this.port_lib$lootTableId;
		}

		@Override
		public void setPortingLibLootTableId(ResourceLocation id) {
			this.port_lib$lootTableId = id;
		}

		@Inject(method = "build", at = @At("RETURN"))
		private void addId(CallbackInfoReturnable<LootTable> cir) {
			cir.getReturnValue().setLootTableId(this.port_lib$lootTableId);
		}
	}
}
