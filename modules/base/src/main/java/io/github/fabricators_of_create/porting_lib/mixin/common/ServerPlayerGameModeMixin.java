package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.block.HarvestableBlock;
import io.github.fabricators_of_create.porting_lib.item.UseFirstBehaviorItem;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.BlockAccessor;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
	@Shadow
	protected ServerLevel level;

	@Shadow
	@Final
	protected ServerPlayer player;

	@Shadow
	private GameType gameModeForPlayer;

	@Inject(
			method = "useItemOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"
			),
			cancellable = true
	)
	public void port_lib$onItemFirstUse(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (heldItem.getItem() instanceof UseFirstBehaviorItem useFirst) {
			UseOnContext ctx = new UseOnContext(player, hand, hit);
			BlockPos pos = ctx.getClickedPos();
			BlockInWorld block = new BlockInWorld(ctx.getLevel(), pos, false);
			if (!player.getAbilities().mayBuild && !heldItem.hasAdventureModePlaceTagForBlock(BuiltInRegistries.BLOCK, block)) {
				cir.setReturnValue(InteractionResult.PASS);
			} else {
				Item item = heldItem.getItem();
				InteractionResult result = useFirst.onItemUseFirst(heldItem, ctx);
				if (result.shouldAwardStats()) {
					player.awardStat(Stats.ITEM_USED.get(item));
				}

				if (result != InteractionResult.PASS) {
					cir.setReturnValue(result);
				}
			}
		}
	}

	@WrapOperation(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean port_lib$canHarvestBlock(ServerPlayer player, BlockState blockstate, Operation<Boolean> operation, BlockPos pos) {
		if (blockstate.getBlock() instanceof HarvestableBlock harvestableBlock)
			return harvestableBlock.canHarvestBlock(blockstate, this.level, pos, player);
		else
			return operation.call(player, blockstate);
	}

	@Unique
	private ThreadLocal<Integer> XP = ThreadLocal.withInitial(() -> -1);

	@ModifyExpressionValue(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canAttackBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"))
	private boolean port_lib$blockBreakHook(boolean original, BlockPos pos) {
		int exp = PortingHooks.onBlockBreakEvent(this.level, this.gameModeForPlayer, this.player, pos);
		XP.set(exp);
		return !(exp == -1);
	}

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;playerDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void port_lib$popXp(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState blockState, BlockEntity blockEntity, Block block, boolean bl) {
		int exp = XP.get();
		if (bl && exp > 0)
			((BlockAccessor)blockState.getBlock()).port_lib$popExperience(level, pos, exp);
		XP.remove();
	}

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"), cancellable = true)
	public void port_lib$destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if(!(this.level.getBlockState(pos).getBlock() instanceof GameMasterBlock && !this.player.canUseGameMasterBlocks()) && player.getMainHandItem().onBlockStartBreak(pos, player))
			cir.setReturnValue(false);
	}
}
