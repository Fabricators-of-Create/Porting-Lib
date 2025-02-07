package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.LootContextExtensions;
import io.github.fabricators_of_create.porting_lib.loot.LootTableIdCondition;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootContext.class)
public abstract class LootContextMixin implements LootContextExtensions {
	@Shadow
	@Nullable
	public abstract <T> T getParamOrNull(LootContextParameter<T> lootContextParam);

	private Identifier queriedLootTableId;

	@Override
	public void setQueriedLootTableId(Identifier queriedLootTableId) {
		if (this.queriedLootTableId == null && queriedLootTableId != null) this.queriedLootTableId = queriedLootTableId;
	}

	@Override
	public Identifier getQueriedLootTableId() {
		return this.queriedLootTableId == null? LootTableIdCondition.UNKNOWN_LOOT_TABLE : this.queriedLootTableId;
	}

	@Override
	public int getLootingModifier() {
		return PortingHooks.getLootingLevel(getParamOrNull(LootContextParameters.THIS_ENTITY), getParamOrNull(LootContextParameters.KILLER_ENTITY), getParamOrNull(LootContextParameters.DAMAGE_SOURCE));
	}
}
