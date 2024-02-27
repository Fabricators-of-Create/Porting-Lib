package io.github.fabricators_of_create.porting_lib.loot.mixin.loottable;

import java.util.Objects;

import io.github.fabricators_of_create.porting_lib.loot.LootCollector;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootTableBuilderExtensions;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootTableExtensions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

@Mixin(LootTable.class)
public class LootTableMixin implements LootTableExtensions {
	@Unique
	private ResourceLocation lootTableId;

	@Unique
	private static final ThreadLocal<LootCollector> lootCollector = new ThreadLocal<>();

	@Override
	public ThreadLocal<LootCollector> port_lib$lootCollector() {
		return lootCollector;
	}

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
