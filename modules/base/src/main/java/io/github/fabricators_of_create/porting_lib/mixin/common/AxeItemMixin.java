package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

// This really should be in the item abilities modules fix this TODO: fix this on 1.21
@Mixin(AxeItem.class)
public class AxeItemMixin {
	@Inject(method = "useOn", at = @At("HEAD"))
	private void shareUseOnContext(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Share("useOnContext") LocalRef<UseOnContext> sharedContext) {
		sharedContext.set(context);
	}

	@WrapOperation(method = "evaluateNewBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/AxeItem;getStripped(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> onStripToolAction(AxeItem instance, BlockState blockState, Operation<Optional<BlockState>> original, @Share("useOnContext") LocalRef<UseOnContext> sharedContext) {
		BlockState eventState = PortingHooks.onToolUse(blockState, sharedContext.get(), ItemAbilities.AXE_STRIP, false);
		return eventState != blockState ? Optional.ofNullable(eventState) : original.call(instance, blockState);
	}

	@WrapOperation(method = "evaluateNewBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WeatheringCopper;getPrevious(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> onScrapeToolAction(BlockState blockState, Operation<Optional<BlockState>> original, @Share("useOnContext") LocalRef<UseOnContext> sharedContext) {
		BlockState eventState = PortingHooks.onToolUse(blockState, sharedContext.get(), ItemAbilities.AXE_SCRAPE, false);
		return eventState != blockState ? Optional.ofNullable(eventState) : original.call(blockState);
	}
}
