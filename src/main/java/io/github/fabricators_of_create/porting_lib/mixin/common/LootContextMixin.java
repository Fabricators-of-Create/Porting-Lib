package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.LootContextExtensions;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.world.level.storage.loot.LootContext;

import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LootContext.class)
public abstract class LootContextMixin implements LootContextExtensions {
	@Shadow
	@Nullable
	public abstract <T> T getParamOrNull(LootContextParam<T> lootContextParam);

	@Override
	public int getLootingModifier() {
		return PortingHooks.getLootingLevel(getParamOrNull(LootContextParams.THIS_ENTITY), getParamOrNull(LootContextParams.KILLER_ENTITY), getParamOrNull(LootContextParams.DAMAGE_SOURCE));
	}
}
