package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.world.item.AxeItem;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

// This really should be in the tool actions modules fix this TODO: fix this on 1.21
@Mixin(AxeItem.class)
public class AxeItemMixin {
	@WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/AxeItem;getStripped(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> onStripToolAction(AxeItem instance, BlockState blockState, Operation<Optional<BlockState>> original, UseOnContext context) {
		BlockState eventState = PortingHooks.onToolUse(blockState, context, ToolActions.AXE_STRIP, false);
		return eventState != blockState ? Optional.ofNullable(eventState) : original.call(instance, blockState);
	}

	@WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WeatheringCopper;getPrevious(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
	private Optional<BlockState> onScrapeToolAction(BlockState blockState, Operation<Optional<BlockState>> original, UseOnContext context) {
		BlockState eventState = PortingHooks.onToolUse(blockState, context, ToolActions.AXE_SCRAPE, false);
		return eventState != blockState ? Optional.ofNullable(eventState) : original.call(blockState);
	}
}
