package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.mojang.authlib.GameProfile;

import io.github.fabricators_of_create.porting_lib.entity.ITeleporter;
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
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
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

	@Shadow
	public abstract CommonPlayerSpawnInfo createCommonSpawnInfo(ServerLevel world);

	@Inject(method = "<init>", at = @At("RETURN"))
	private void port_lib$init(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, CallbackInfo ci) {
		ServerPlayerCreationCallback.EVENT.invoker().onCreate((ServerPlayer) (Object) this);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void port_lib$clientStartTickEvent(CallbackInfo ci) {
		PlayerTickEvents.START.invoker().onStartOfPlayerTick(this);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void port_lib$clientEndTickEvent(CallbackInfo ci) {
		PlayerTickEvents.END.invoker().onEndOfPlayerTick(this);
	}

	@Nullable
	@Override
	public Entity changeDimension(ServerLevel destination, ITeleporter teleporter) {
//		if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, destination.dimension())) return null;
		this.isChangingDimension = true;
		ServerLevel serverlevel = this.serverLevel();
		ResourceKey<Level> resourcekey = serverlevel.dimension();
		if (resourcekey == Level.END && destination.dimension() == Level.OVERWORLD && teleporter.isVanilla()) { //Forge: Fix non-vanilla teleporters triggering end credits
			this.unRide();
			this.serverLevel().removePlayerImmediately((ServerPlayer) (Object) this, Entity.RemovalReason.CHANGED_DIMENSION);
			if (!this.wonGame) {
				this.wonGame = true;
				this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, this.seenCredits ? 0.0F : 1.0F));
				this.seenCredits = true;
			}

			return this;
		} else {
			LevelData leveldata = destination.getLevelData();
			this.connection.send(new ClientboundRespawnPacket(createCommonSpawnInfo(destination), ClientboundRespawnPacket.KEEP_ALL_DATA));
			this.connection.send(new ClientboundChangeDifficultyPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
			PlayerList playerlist = this.server.getPlayerList();
			playerlist.sendPlayerPermissionLevel((ServerPlayer) (Object) this);
			serverlevel.removePlayerImmediately((ServerPlayer) (Object) this, Entity.RemovalReason.CHANGED_DIMENSION);
			this.unsetRemoved();
			PortalInfo portalinfo = teleporter.getPortalInfo(this, destination, this::findDimensionEntryPoint);
			if (portalinfo != null) {
				Entity e = teleporter.placeEntity(this, serverlevel, destination, this.getYRot(), spawnPortal -> {//Forge: Start vanilla logic
					serverlevel.getProfiler().push("moving");
					if (resourcekey == Level.OVERWORLD && destination.dimension() == Level.NETHER) {
						this.enteredNetherPosition = this.position();
					} else if (spawnPortal && destination.dimension() == Level.END) {
						this.createEndPlatform(destination, BlockPos.containing(portalinfo.pos));
					}

					serverlevel.getProfiler().pop();
					serverlevel.getProfiler().push("placing");
					this.setLevel(destination);
					destination.addDuringPortalTeleport((ServerPlayer) (Object) this);
					this.setRot(portalinfo.yRot, portalinfo.xRot);
					this.moveTo(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z);
					serverlevel.getProfiler().pop();
					this.triggerDimensionChangeTriggers(serverlevel);
					return this;//forge: this is part of the ITeleporter patch
				});//Forge: End vanilla logic
				if (e != this) throw new java.lang.IllegalArgumentException(String.format(java.util.Locale.ENGLISH, "Teleporter %s returned not the player entity but instead %s, expected PlayerEntity %s", teleporter, e, this));
				this.connection.send(new ClientboundPlayerAbilitiesPacket(this.getAbilities()));
				playerlist.sendLevelInfo((ServerPlayer) (Object) this, destination);
				playerlist.sendAllPlayerInfo((ServerPlayer) (Object) this);

				for(MobEffectInstance mobeffectinstance : this.getActiveEffects()) {
					this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), mobeffectinstance));
				}

				if (teleporter.playTeleportSound((ServerPlayer) (Object) this, serverlevel, destination))
					this.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
				this.lastSentExp = -1;
				this.lastSentHealth = -1.0F;
				this.lastSentFood = -1;
//				net.minecraftforge.event.ForgeEventFactory.firePlayerChangedDimensionEvent(this, resourcekey, destination.dimension());
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
}
