package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.extensions.LootTableExtensions;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableExtensions {
	private ResourceLocation lootTableId;

	@Override
	public void setLootTableId(final ResourceLocation id) {
		if (this.lootTableId != null) throw new IllegalStateException("Attempted to rename loot table from '" + this.lootTableId + "' to '" + id + "': this is not supported");
		this.lootTableId = java.util.Objects.requireNonNull(id);
	}

	@Override
	public ResourceLocation getLootTableId() { return this.lootTableId; }

	@ModifyReturnValue(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Ljava/util/List;", at = @At("RETURN"))
	public List<ItemStack> port_lib$modifyGlobalLootTable(List<ItemStack> list, LootContext context) {
		return PortingHooks.modifyLoot(getLootTableId(), list, context);
	}
}
