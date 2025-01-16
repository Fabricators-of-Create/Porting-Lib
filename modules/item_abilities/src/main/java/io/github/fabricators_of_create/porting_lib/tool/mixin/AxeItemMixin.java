package io.github.fabricators_of_create.porting_lib.tool.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilityHooks;
import io.github.fabricators_of_create.porting_lib.tool.extensions.VanillaItemAbilityItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public class AxeItemMixin implements VanillaItemAbilityItem {
    @Unique
    private UseOnContext porting_lib_item_abilities$context;

    @Inject(method = "useOn", at = @At("HEAD"))
    private void shareUseOnContext(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        porting_lib_item_abilities$context = context;
    }

    @WrapOperation(method = "evaluateNewBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/AxeItem;getStripped(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
    private Optional<BlockState> onStripToolAction(AxeItem instance, BlockState blockState, Operation<Optional<BlockState>> original) {
        BlockState eventState = ItemAbilityHooks.onToolUse(blockState, porting_lib_item_abilities$context, ItemAbilities.AXE_STRIP, false);
        return eventState != blockState ? Optional.ofNullable(eventState) : original.call(instance, blockState);
    }

    @WrapOperation(method = "evaluateNewBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WeatheringCopper;getPrevious(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
    private Optional<BlockState> onScrapeToolAction(BlockState blockState, Operation<Optional<BlockState>> original) {
        BlockState eventState = ItemAbilityHooks.onToolUse(blockState, porting_lib_item_abilities$context, ItemAbilities.AXE_SCRAPE, false);
        return eventState != blockState ? Optional.ofNullable(eventState) : original.call(blockState);
    }

    @Inject(method = "useOn", at = @At("RETURN"))
    private void clearUseOnContext(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        porting_lib_item_abilities$context = null;
    }

    @Override
    public boolean port_lib$canPerformAction(ItemStack stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability);
    }
}
