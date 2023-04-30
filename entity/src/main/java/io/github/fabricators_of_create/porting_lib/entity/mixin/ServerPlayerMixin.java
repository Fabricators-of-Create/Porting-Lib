package io.github.fabricators_of_create.porting_lib.entity.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerTickEvents;
import io.github.fabricators_of_create.porting_lib.entity.extensions.EntityExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements EntityExtensions {
	public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(level, blockPos, f, gameProfile);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void onTickStart(CallbackInfo ci) {
		PlayerTickEvents.START.invoker().onPlayerTickStart(this);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void onTickEnd(CallbackInfo ci) {
		PlayerTickEvents.END.invoker().onPlayerTickEnd(this);
	}

	@Inject(method = "restoreFrom", at = @At("TAIL"))
	private void copyPersistentData(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
		CompoundTag oldData = oldPlayer.getCustomData();
		if (oldData.contains("PlayerPersisted", Tag.TAG_COMPOUND)) {
			CompoundTag persistent = oldData.getCompound("PlayerPersisted");
			CompoundTag thisData = this.getCustomData();
			thisData.put("PlayerPersisted", persistent);
		}
	}

}
