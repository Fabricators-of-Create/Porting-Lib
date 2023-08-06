package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;

import io.github.fabricators_of_create.porting_lib.util.ConsumableValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(Block.class)
public class BlockMixin {
	@Unique
	private static final ThreadLocal<ConsumableValue<Player>> playerRef = ThreadLocal.withInitial(ConsumableValue::new);

	@Inject(method = "playerDestroy", at = @At("HEAD"))
	private void grabPlayer(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
		playerRef.get().set(player);
	}

	@WrapOperation(
			method = "popExperience",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
			)
	)
	private void modifyDroppedExp(ServerLevel level, Vec3 spawnPos, int amount, Operation<Void> original,
								  ServerLevel levelAgain, BlockPos pos, int amountAgain) {
		Player player = playerRef.get().consume();
		BlockState state = level.getBlockState(pos);
		int newAmount = BlockEvents.MODIFY_EXP.invoker().modifyExp(level, state, pos, player, amount);
		original.call(level, spawnPos, newAmount);
	}
}
