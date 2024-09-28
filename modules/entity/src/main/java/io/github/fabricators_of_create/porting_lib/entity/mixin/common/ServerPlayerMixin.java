package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.mojang.authlib.GameProfile;

import io.github.fabricators_of_create.porting_lib.entity.ITeleporter;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingDeathEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerTickEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.ServerPlayerCreationCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.storage.LevelData;

import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
	@Shadow
	private boolean isChangingDimension;

	public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Shadow
	public abstract ServerLevel serverLevel();

	@Shadow
	public ServerGamePacketListenerImpl connection;

	@Shadow
	@Final
	public MinecraftServer server;

	@Shadow
	public boolean wonGame;

	@Shadow
	private boolean seenCredits;

	@Shadow
	@Final
	public ServerPlayerGameMode gameMode;

	@Shadow
	protected abstract void triggerDimensionChangeTriggers(ServerLevel origin);

	@Shadow
	private int lastSentExp;

	@Shadow
	private float lastSentHealth;

	@Shadow
	private int lastSentFood;

	@Shadow
	@org.jetbrains.annotations.Nullable
	private Vec3 enteredNetherPosition;

	@Shadow
	protected abstract void createEndPlatform(ServerLevel world, BlockPos centerPos);

	@Inject(method = "<init>", at = @At("RETURN"))
	private void port_lib$init(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, CallbackInfo ci) {
		ServerPlayerCreationCallback.EVENT.invoker().onCreate((ServerPlayer) (Object) this);
	}

	@Nullable
	@Override
	public Entity changeDimension(ServerLevel p_9180_, ITeleporter teleporter) {
//		if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, p_9180_.dimension())) return null;
		this.isChangingDimension = true;
		ServerLevel serverlevel = this.serverLevel();
		ResourceKey<Level> resourcekey = serverlevel.dimension();
		if (resourcekey == Level.END && p_9180_.dimension() == Level.OVERWORLD && teleporter.isVanilla()) { //Forge: Fix non-vanilla teleporters triggering end credits
			this.unRide();
			this.serverLevel().removePlayerImmediately((ServerPlayer) (Object) this, Entity.RemovalReason.CHANGED_DIMENSION);
			if (!this.wonGame) {
				this.wonGame = true;
				this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, this.seenCredits ? 0.0F : 1.0F));
				this.seenCredits = true;
			}

			return this;
		} else {
			LevelData leveldata = p_9180_.getLevelData();
			this.connection.send(new ClientboundRespawnPacket(p_9180_.dimensionTypeId(), p_9180_.dimension(), BiomeManager.obfuscateSeed(p_9180_.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), p_9180_.isDebug(), p_9180_.isFlat(), (byte) 3, this.getLastDeathLocation(), getPortalCooldown()));
			this.connection.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
			PlayerList playerlist = this.server.getPlayerList();
			playerlist.sendPlayerPermissionLevel((ServerPlayer) (Object) this);
			serverlevel.removePlayerImmediately((ServerPlayer) (Object) this, Entity.RemovalReason.CHANGED_DIMENSION);
			this.unsetRemoved();
			PortalInfo portalinfo = teleporter.getPortalInfo(this, p_9180_, this::findDimensionEntryPoint);
			if (portalinfo != null) {
				Entity e = teleporter.placeEntity(this, serverlevel, p_9180_, this.getYRot(), spawnPortal -> {//Forge: Start vanilla logic
					serverlevel.getProfiler().push("moving");
					if (resourcekey == Level.OVERWORLD && p_9180_.dimension() == Level.NETHER) {
						this.enteredNetherPosition = this.position();
					} else if (spawnPortal && p_9180_.dimension() == Level.END) {
						this.createEndPlatform(p_9180_, BlockPos.containing(portalinfo.pos));
					}

					serverlevel.getProfiler().pop();
					serverlevel.getProfiler().push("placing");
					this.setLevel(p_9180_);
					p_9180_.addDuringPortalTeleport((ServerPlayer) (Object) this);
					this.setRot(portalinfo.yRot, portalinfo.xRot);
					this.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z);
					serverlevel.getProfiler().pop();
					this.triggerDimensionChangeTriggers(serverlevel);
					return this;//forge: this is part of the ITeleporter patch
				});//Forge: End vanilla logic
				if (e != this) throw new java.lang.IllegalArgumentException(String.format(java.util.Locale.ENGLISH, "Teleporter %s returned not the player entity but instead %s, expected PlayerEntity %s", teleporter, e, this));
				this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
				playerlist.sendLevelInfo((ServerPlayer) (Object) this, p_9180_);
				playerlist.sendAllPlayerInfo((ServerPlayer) (Object) this);

				for(MobEffectInstance mobeffectinstance : this.getActiveEffects()) {
					this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), mobeffectinstance));
				}

				if (teleporter.playTeleportSound((ServerPlayer) (Object) this, serverlevel, p_9180_))
					this.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
				this.lastSentExp = -1;
				this.lastSentHealth = -1.0F;
				this.lastSentFood = -1;
//				net.minecraftforge.event.ForgeEventFactory.firePlayerChangedDimensionEvent(this, resourcekey, p_9180_.dimension());
			}

			return this;
		}
	}

	@Inject(method = "restoreFrom", at = @At("TAIL"))
	private void port_lib$copyPersistentData(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
		CompoundTag oldData = oldPlayer.getCustomData();
		CompoundTag persistent = oldData.getCompound("PlayerPersisted");
		if (persistent != null) {
			CompoundTag thisData = this.getCustomData();
			thisData.put("PlayerPersisted", persistent);
		}
	}

	@Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;)V", shift = At.Shift.AFTER), cancellable = true)
	private void onPlayerDie(DamageSource cause, CallbackInfo ci) {
		LivingDeathEvent event = new LivingDeathEvent((ServerPlayer) (Object) this, cause);
		event.sendEvent();
		if (event.isCanceled())
			ci.cancel();
	}
}
