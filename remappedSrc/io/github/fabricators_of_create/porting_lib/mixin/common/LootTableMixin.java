package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.List;
import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.extensions.LootTableExtensions;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableExtensions {
	@Unique
	private Identifier lootTableId;

	@Override
	public void setLootTableId(final Identifier id) {
		if (this.lootTableId != null)
			throw new IllegalStateException("Attempted to rename loot table from '" + this.lootTableId + "' to '" + id + "': this is not supported");
		this.lootTableId = Objects.requireNonNull(id);
	}

	@Override
	public Identifier getLootTableId() {
		return this.lootTableId;
	}

	@ModifyReturnValue(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Ljava/util/List;", at = @At("RETURN"))
	private List<ItemStack> applyGlobalModifiers(List<ItemStack> list, LootContext context) {
		return PortingHooks.modifyLoot(getLootTableId(), list, context);
	}
}
