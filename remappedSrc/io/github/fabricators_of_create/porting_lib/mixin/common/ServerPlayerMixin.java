package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.PlayerTickEvents;

import io.github.fabricators_of_create.porting_lib.extensions.EntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.ITeleporter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import io.github.fabricators_of_create.porting_lib.event.common.ServerPlayerCreationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;



@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends PlayerEntity implements EntityExtensions {

	@Shadow
	public abstract ServerWorld getLevel();

	@Shadow
	private boolean isChangingDimension;

	@Shadow
	private boolean seenCredits;

	@Shadow
	public ServerPlayNetworkHandler connection;

	@Shadow
	public boolean wonGame;

	@Shadow
	@Final
	public MinecraftServer server;

	@Shadow
	@Final
	public ServerPlayerInteractionManager gameMode;

	@Shadow
	@org.jetbrains.annotations.Nullable
	private Vec3d enteredNetherPosition;

	@Shadow
	protected abstract void createEndPlatform(ServerWorld world, BlockPos centerPos);

	@Shadow
	public abstract void setLevel(ServerWorld world);

	@Shadow
	protected abstract void triggerDimensionChangeTriggers(ServerWorld origin);

	@Shadow
	private int lastSentExp;

	@Shadow
	private float lastSentHealth;

	@Shadow
	private int lastSentFood;

	public ServerPlayerMixin(World level, BlockPos blockPos, float f, GameProfile gameProfile) {
		super(level, blockPos, f, gameProfile);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void port_lib$init(MinecraftServer minecraftServer, ServerWorld serverLevel, GameProfile gameProfile, CallbackInfo ci) {
		ServerPlayerCreationCallback.EVENT.invoker().onCreate((ServerPlayerEntity) (Object) this);
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
	public Entity changeDimension(ServerWorld p_9180_, ITeleporter teleporter) {
//		if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, p_9180_.dimension())) return null;
		this.isChangingDimension = true;
		ServerWorld serverlevel = this.getLevel();
		RegistryKey<World> resourcekey = serverlevel.getRegistryKey();
		if (resourcekey == World.END && p_9180_.getRegistryKey() == World.OVERWORLD && teleporter.isVanilla()) { //Forge: Fix non-vanilla teleporters triggering end credits
			this.detach();
			this.getLevel().removePlayer((ServerPlayerEntity) (Object) this, Entity.RemovalReason.CHANGED_DIMENSION);
			if (!this.wonGame) {
				this.wonGame = true;
				this.connection.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_WON, this.seenCredits ? 0.0F : 1.0F));
				this.seenCredits = true;
			}

			return this;
		} else {
			WorldProperties leveldata = p_9180_.getLevelProperties();
			this.connection.sendPacket(new PlayerRespawnS2CPacket(p_9180_.dimensionTypeRegistration(), p_9180_.getRegistryKey(), BiomeAccess.hashSeed(p_9180_.getSeed()), this.gameMode.getGameMode(), this.gameMode.getPreviousGameMode(), p_9180_.isDebugWorld(), p_9180_.isFlat(), true));
			this.connection.sendPacket(new DifficultyS2CPacket(leveldata.getDifficulty(), leveldata.isDifficultyLocked()));
			PlayerManager playerlist = this.server.getPlayerManager();
			playerlist.sendCommandTree((ServerPlayerEntity) (Object) this);
			serverlevel.removePlayer((ServerPlayerEntity) (Object) this, Entity.RemovalReason.CHANGED_DIMENSION);
			this.unsetRemoved();
			TeleportTarget portalinfo = teleporter.getPortalInfo(this, p_9180_, this::getTeleportTarget);
			if (portalinfo != null) {
				Entity e = teleporter.placeEntity(this, serverlevel, p_9180_, this.getYaw(), spawnPortal -> {//Forge: Start vanilla logic
					serverlevel.getProfiler().push("moving");
					if (resourcekey == World.OVERWORLD && p_9180_.getRegistryKey() == World.NETHER) {
						this.enteredNetherPosition = this.getPos();
					} else if (spawnPortal && p_9180_.getRegistryKey() == World.END) {
						this.createEndPlatform(p_9180_, new BlockPos(portalinfo.position));
					}

					serverlevel.getProfiler().pop();
					serverlevel.getProfiler().push("placing");
					this.setLevel(p_9180_);
					p_9180_.onPlayerChangeDimension((ServerPlayerEntity) (Object) this);
					this.setRotation(portalinfo.yaw, portalinfo.pitch);
					this.refreshPositionAfterTeleport(portalinfo.position.x, portalinfo.position.y, portalinfo.position.z);
					serverlevel.getProfiler().pop();
					this.triggerDimensionChangeTriggers(serverlevel);
					return this;//forge: this is part of the ITeleporter patch
				});//Forge: End vanilla logic
				if (e != this) throw new java.lang.IllegalArgumentException(String.format(java.util.Locale.ENGLISH, "Teleporter %s returned not the player entity but instead %s, expected PlayerEntity %s", teleporter, e, this));
				this.connection.sendPacket(new PlayerAbilitiesS2CPacket(this.getAbilities()));
				playerlist.sendWorldInfo((ServerPlayerEntity) (Object) this, p_9180_);
				playerlist.sendPlayerStatus((ServerPlayerEntity) (Object) this);

				for(StatusEffectInstance mobeffectinstance : this.getStatusEffects()) {
					this.connection.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), mobeffectinstance));
				}

				if (teleporter.playTeleportSound((ServerPlayerEntity) (Object) this, serverlevel, p_9180_))
					this.connection.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
				this.lastSentExp = -1;
				this.lastSentHealth = -1.0F;
				this.lastSentFood = -1;
//				net.minecraftforge.event.ForgeEventFactory.firePlayerChangedDimensionEvent(this, resourcekey, p_9180_.dimension());
			}

			return this;
		}
	}
}
