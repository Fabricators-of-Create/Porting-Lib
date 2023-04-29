package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.util.TierSortingRegistry;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin extends TieredItem {
	@Shadow
	@Final
	private TagKey<Block> blocks;

	public DiggerItemMixin(Tier tier, Properties properties) {
		super(tier, properties);
	}

	@Inject(method = "isCorrectToolForDrops", at = @At("HEAD"), cancellable = true)
	private void checkSortedTiers(BlockState state, CallbackInfoReturnable<Boolean> cir) {
		Tier tier = getTier();
		if (!(tier instanceof Tiers) && TierSortingRegistry.isTierSorted(tier)) {
			cir.setReturnValue(state.is(this.blocks) && TierSortingRegistry.isCorrectTierForDrops(tier, state));
		}
	}
}
