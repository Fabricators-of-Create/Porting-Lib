package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerInteractionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
	@Shadow
	public abstract boolean isCreative();

	@Shadow
	@Final
	protected ServerPlayer player;

	@Inject(method = "handleBlockBreakAction", at = @At("HEAD"), cancellable = true)
	public void port_lib$blockBreak(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int worldHeight, int i, CallbackInfo ci) {
		PlayerInteractionEvents.LeftClickBlock event = new PlayerInteractionEvents.LeftClickBlock(player, pos, direction);
		event.sendEvent();
		if (event.isCanceled() || (!this.isCreative() && event.getResult() == BaseEvent.Result.DENY)) {
			ci.cancel();
		}
	}
}
