package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import io.github.fabricators_of_create.porting_lib.extensions.ItemStackExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.item.UseFirstBehaviorItem;
import net.minecraft.block.OperatorBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerGameModeMixin {
	@Shadow
	protected ServerWorld level;

	@Shadow
	@Final
	protected ServerPlayerEntity player;

	@Inject(
			method = "useItemOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
					ordinal = 0,
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	public void port_lib$onItemFirstUse(ServerPlayerEntity serverPlayer, World level, ItemStack itemStack, Hand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> cir) {
		if (itemStack.getItem() instanceof UseFirstBehaviorItem first) {
			ItemUsageContext useoncontext = new ItemUsageContext(serverPlayer, interactionHand, blockHitResult);
			ActionResult result = first.onItemUseFirst(itemStack, useoncontext);
			if (result != ActionResult.PASS) cir.setReturnValue(result);
		}
	}

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"), cancellable = true)
	public void port_lib$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if(!(this.level.getBlockState(pos).getBlock() instanceof OperatorBlock && !this.player.isCreativeLevelTwoOp()) && ((ItemStackExtensions)(Object)player.getMainHandStack()).onBlockStartBreak(pos, player))
			cir.setReturnValue(false);
	}

	@Inject(method = "handleBlockBreakAction", at = @At("HEAD"))
	public void port_lib$blockBreak(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo ci) {
		BlockEvents.LEFT_CLICK_BLOCK.invoker().onLeftClickBlock(player, pos, direction);
	}
}
