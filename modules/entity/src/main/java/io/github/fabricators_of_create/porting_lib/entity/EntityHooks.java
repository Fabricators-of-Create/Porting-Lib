package io.github.fabricators_of_create.porting_lib.entity;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityMountEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityStruckByLightningEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityTeleportEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDeathEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingAttackEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDropsEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityUseItemEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingExperienceDropEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingKnockBackEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.MobEffectEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.ShieldBlockEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.AttackEntityEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.CriticalHitEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerDestroyItemEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerInteractEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.ProjectileImpactEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingChangeTargetEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDamageEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingFallEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingUseTotemEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.tick.EntityTickEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.tick.PlayerTickEvent;
import io.github.fabricators_of_create.porting_lib.entity.mixin.accessor.PlayerDataStorageAccessor;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;

public final class EntityHooks {
//	public static boolean doPlayerHarvestCheck(Player player, BlockState state, BlockGetter level, BlockPos pos) {
//		// Call deprecated hasCorrectToolForDrops overload for a fallback value, in turn the non-deprecated overload calls this method
//		boolean vanillaValue = player.hasCorrectToolForDrops(state);
//		PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, level, pos, vanillaValue);
//		event.sendEvent();
//		return event.canHarvest();
//	}

	public static float getBreakSpeed(Player player, BlockState state, float original, BlockPos pos) {
		PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, state, original, pos);
		event.sendEvent();
		return (event.isCanceled() ? -1 : event.getNewSpeed());
	}

	public static void onPlayerDestroyItem(Player player, ItemStack stack, @Nullable InteractionHand hand) {
		new PlayerDestroyItemEvent(player, stack, hand).sendEvent();
	}

	public static int getExperienceDrop(LivingEntity entity, Player attackingPlayer, int originalExperience) {
		LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
		event.sendEvent();
		if (event.isCanceled()) {
			return 0;
		}
		return event.getDroppedExperience();
	}

	public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting) {
		EntityMountEvent event = new EntityMountEvent(entityMounting, entityBeingMounted, entityMounting.level(), isMounting);
		event.sendEvent();

		if (event.isCanceled()) {
			entityMounting.absMoveTo(entityMounting.getX(), entityMounting.getY(), entityMounting.getZ(), entityMounting.yRotO, entityMounting.xRotO);
			return false;
		} else
			return true;
	}

	public static LivingChangeTargetEvent onLivingChangeTarget(LivingEntity entity, LivingEntity originalTarget, LivingChangeTargetEvent.ILivingTargetType targetType) {
		LivingChangeTargetEvent event = new LivingChangeTargetEvent(entity, originalTarget, targetType);
		event.sendEvent();

		return event;
	}

	public static boolean onLivingAttack(LivingEntity entity, DamageSource src, float amount) {
		return entity instanceof Player || !new LivingAttackEvent(entity, src, amount).post();
	}

	public static boolean onPlayerAttack(LivingEntity entity, DamageSource src, float amount) {
		return !new LivingAttackEvent(entity, src, amount).post();
	}

	public static LivingKnockBackEvent onLivingKnockBack(LivingEntity target, float strength, double ratioX, double ratioZ) {
		LivingKnockBackEvent event = new LivingKnockBackEvent(target, strength, ratioX, ratioZ);
		event.sendEvent();
		return event;
	}

	public static boolean onLivingUseTotem(LivingEntity entity, DamageSource damageSource, ItemStack totem, InteractionHand hand) {
		LivingUseTotemEvent event = new LivingUseTotemEvent(entity, damageSource, totem, hand);
		event.sendEvent();
		return !event.isCanceled();
	}

	public static float onLivingHurt(LivingEntity entity, DamageSource src, float amount) {
		LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
		event.sendEvent();
		return (event.isCanceled() ? 0 : event.getAmount());
	}

//	public static float onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
//		LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
//		event.sendEvent();
//		return (event.isCanceled() ? 0 : event.getAmount());
//	}

	public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
		var event = new LivingDeathEvent(entity, src);
		event.sendEvent();
		return event.isCanceled();
	}

	public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, boolean recentlyHit) {
		return new LivingDropsEvent(entity, source, drops, recentlyHit).post();
	}

	@Nullable
	public static float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier) {
		LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
		event.sendEvent();
		return event.isCanceled() ? null : new float[] { event.getDistance(), event.getDamageMultiplier() };
	}

	public static double getEntityVisibilityMultiplier(LivingEntity entity, Entity lookingEntity, double originalMultiplier) {
		LivingEvent.LivingVisibilityEvent event = new LivingEvent.LivingVisibilityEvent(entity, lookingEntity, originalMultiplier);
		event.sendEvent();
		return Math.max(0, event.getVisibilityModifier());
	}

	public static void onLivingJump(LivingEntity entity) {
		new LivingEvent.LivingJumpEvent(entity).sendEvent();
	}

	public static boolean onPlayerAttackTarget(Player player, Entity target) {
		var event = new AttackEntityEvent(player, target);
		event.sendEvent();
		if (event.isCanceled())
			return false;
		ItemStack stack = player.getMainHandItem();
		return stack.isEmpty() || !stack.getItem().onLeftClickEntity(stack, player, target);
	}

	@Nullable
	public static InteractionResult onInteractEntityAt(Player player, Entity entity, HitResult ray, InteractionHand hand) {
		Vec3 vec3d = ray.getLocation().subtract(entity.position());
		return onInteractEntityAt(player, entity, vec3d, hand);
	}

	@Nullable
	public static InteractionResult onInteractEntityAt(Player player, Entity entity, Vec3 vec3d, InteractionHand hand) {
		PlayerInteractEvent.EntityInteractSpecific evt = new PlayerInteractEvent.EntityInteractSpecific(player, hand, entity, vec3d);
		evt.sendEvent();
		return evt.isCanceled() ? evt.getCancellationResult() : null;
	}

	@Nullable
	public static InteractionResult onInteractEntity(Player player, Entity entity, InteractionHand hand) {
		PlayerInteractEvent.EntityInteract evt = new PlayerInteractEvent.EntityInteract(player, hand, entity);
		evt.sendEvent();
		return evt.isCanceled() ? evt.getCancellationResult() : null;
	}

	@Nullable
	public static InteractionResult onItemRightClick(Player player, InteractionHand hand) {
		PlayerInteractEvent.RightClickItem evt = new PlayerInteractEvent.RightClickItem(player, hand);
		evt.sendEvent();
		return evt.isCanceled() ? evt.getCancellationResult() : null;
	}

	public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(Player player, BlockPos pos, Direction face, ServerboundPlayerActionPacket.Action action) {
		PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face, PlayerInteractEvent.LeftClickBlock.Action.convert(action));
		evt.sendEvent();
		return evt;
	}

	public static PlayerInteractEvent.LeftClickBlock onClientMineHold(Player player, BlockPos pos, Direction face) {
		PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face, PlayerInteractEvent.LeftClickBlock.Action.CLIENT_HOLD);
		evt.sendEvent();
		return evt;
	}

	public static PlayerInteractEvent.RightClickBlock onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
		PlayerInteractEvent.RightClickBlock evt = new PlayerInteractEvent.RightClickBlock(player, hand, pos, hitVec);
		evt.sendEvent();
		return evt;
	}

	public static void onEmptyClick(Player player, InteractionHand hand) {
		new PlayerInteractEvent.RightClickEmpty(player, hand).sendEvent();
	}

	public static void onEmptyLeftClick(Player player) {
		new PlayerInteractEvent.LeftClickEmpty(player).sendEvent();
	}

	public static EntityEvent.Size getEntitySizeForge(Entity entity, Pose pose, EntityDimensions size, float eyeHeight) {
		EntityEvent.Size evt = new EntityEvent.Size(entity, pose, size, eyeHeight);
		evt.sendEvent();
		return evt;
	}

	public static EntityEvent.Size getEntitySizeForge(Entity entity, Pose pose, EntityDimensions oldSize, EntityDimensions newSize, float newEyeHeight) {
		EntityEvent.Size evt = new EntityEvent.Size(entity, pose, oldSize, newSize, entity.getEyeHeight(), newEyeHeight);
		evt.sendEvent();
		return evt;
	}

	public static EntityTeleportEvent.TeleportCommand onEntityTeleportCommand(Entity entity, double targetX, double targetY, double targetZ) {
		EntityTeleportEvent.TeleportCommand event = new EntityTeleportEvent.TeleportCommand(entity, targetX, targetY, targetZ);
		event.sendEvent();
		return event;
	}

	public static EntityTeleportEvent.SpreadPlayersCommand onEntityTeleportSpreadPlayersCommand(Entity entity, double targetX, double targetY, double targetZ) {
		EntityTeleportEvent.SpreadPlayersCommand event = new EntityTeleportEvent.SpreadPlayersCommand(entity, targetX, targetY, targetZ);
		event.sendEvent();
		return event;
	}

	public static EntityTeleportEvent.EnderEntity onEnderTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) { // TODO: implement on shulker
		EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity(entity, targetX, targetY, targetZ);
		event.sendEvent();
		return event;
	}

	@ApiStatus.Internal
	public static EntityTeleportEvent.EnderPearl onEnderPearlLand(ServerPlayer entity, double targetX, double targetY, double targetZ, ThrownEnderpearl pearlEntity, float attackDamage, HitResult hitResult) {
		EntityTeleportEvent.EnderPearl event = new EntityTeleportEvent.EnderPearl(entity, targetX, targetY, targetZ, pearlEntity, attackDamage, hitResult);
		event.sendEvent();
		return event;
	}

	public static EntityTeleportEvent.ChorusFruit onChorusFruitTeleport(LivingEntity entity, double targetX, double targetY, double targetZ) {
		EntityTeleportEvent.ChorusFruit event = new EntityTeleportEvent.ChorusFruit(entity, targetX, targetY, targetZ);
		event.sendEvent();
		return event;
	}

	public static void firePlayerLoggedIn(Player player) {
		new PlayerEvent.PlayerLoggedInEvent(player).sendEvent();
	}

	public static void firePlayerLoggedOut(Player player) {
		new PlayerEvent.PlayerLoggedOutEvent(player).sendEvent();
	}

	public static boolean onEntityStruckByLightning(Entity entity, LightningBolt bolt) {
		EntityStruckByLightningEvent event = new EntityStruckByLightningEvent(entity, bolt);
		event.sendEvent();
		return event.isCanceled();
	}

	public static int onItemUseStart(LivingEntity entity, ItemStack item, int duration) {
		var event = new LivingEntityUseItemEvent.Start(entity, item, duration);
		return event.post() ? -1 : event.getDuration();
	}

	public static int onItemUseTick(LivingEntity entity, ItemStack item, int duration) {
		var event = new LivingEntityUseItemEvent.Tick(entity, item, duration);
		return event.post() ? -1 : event.getDuration();
	}

	public static boolean onUseItemStop(LivingEntity entity, ItemStack item, int duration) {
		return new LivingEntityUseItemEvent.Stop(entity, item, duration).post();
	}

	public static ItemStack onItemUseFinish(LivingEntity entity, ItemStack item, int duration, ItemStack result) {
		LivingEntityUseItemEvent.Finish event = new LivingEntityUseItemEvent.Finish(entity, item, duration, result);
		event.sendEvent();
		return event.getResultStack();
	}

	public static void onStartEntityTracking(Entity entity, Player player) {
		new PlayerEvent.StartTracking(player, entity).sendEvent();
	}

	public static void onStopEntityTracking(Entity entity, Player player) {
		new PlayerEvent.StopTracking(player, entity).sendEvent();
	}

	public static void firePlayerLoadingEvent(Player player, File playerDirectory, String uuidString) {
		new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString).sendEvent();
	}

	public static void firePlayerSavingEvent(Player player, File playerDirectory, String uuidString) {
		new PlayerEvent.SaveToFile(player, playerDirectory, uuidString).sendEvent();
	}

	public static void firePlayerLoadingEvent(Player player, PlayerDataStorage playerFileData, String uuidString) {
		new PlayerEvent.LoadFromFile(player, ((PlayerDataStorageAccessor) playerFileData).getPlayerDir(), uuidString).sendEvent();
	}

	public static boolean onProjectileImpact(Projectile projectile, HitResult ray) {
		ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, ray);
		event.sendEvent();
		return event.isCanceled();
	}

	public static void firePlayerCraftingEvent(Player player, ItemStack crafted, Container craftMatrix) {
		new PlayerEvent.ItemCraftedEvent(player, crafted, craftMatrix).sendEvent();
	}

	public static void firePlayerSmeltedEvent(Player player, ItemStack smelted) {
		new PlayerEvent.ItemSmeltedEvent(player, smelted).sendEvent();
	}

	/**
	 * Fires {@link EntityTickEvent.Pre}. Called from the head of {@link LivingEntity#tick()}.
	 *
	 * @param entity The entity being ticked
	 * @return The event
	 */
	public static EntityTickEvent.Pre fireEntityTickPre(Entity entity) {
		EntityTickEvent.Pre event = new EntityTickEvent.Pre(entity);
		event.sendEvent();
		return event;
	}

	/**
	 * Fires {@link EntityTickEvent.Post}. Called from the tail of {@link LivingEntity#tick()}.
	 *
	 * @param entity The entity being ticked
	 */
	public static void fireEntityTickPost(Entity entity) {
		new EntityTickEvent.Post(entity).sendEvent();
	}

	/**
	 * Fires {@link PlayerTickEvent.Pre}. Called from the head of {@link Player#tick()}.
	 *
	 * @param player The player being ticked
	 */
	public static void firePlayerTickPre(Player player) {
		new PlayerTickEvent.Pre(player).sendEvent();
	}

	/**
	 * Fires {@link PlayerTickEvent.Post}. Called from the tail of {@link Player#tick()}.
	 *
	 * @param player The player being ticked
	 */
	public static void firePlayerTickPost(Player player) {
		new PlayerTickEvent.Post(player).sendEvent();
	}

	/**
	 * Fires the {@link CriticalHitEvent} and returns the resulting event.
	 *
	 * @param player          The attacking player
	 * @param target          The attack target
	 * @param vanillaCritical If the attack would have been a critical hit by vanilla's rules in {@link Player#attack(Entity)}.
	 * @param damageModifier  The base damage modifier. Vanilla critical hits have a damage modifier of 1.5.
	 */
	public static CriticalHitEvent fireCriticalHit(Player player, Entity target, boolean vanillaCritical, float damageModifier) {
		CriticalHitEvent event = new CriticalHitEvent(player, target, damageModifier, vanillaCritical);
		event.sendEvent();
		return event;
	}

	public static void onEntityEnterSection(Entity entity, long packedOldPos, long packedNewPos) {
		new EntityEvent.EnteringSection(entity, packedOldPos, packedNewPos).sendEvent();
	}

	public static ShieldBlockEvent onShieldBlock(LivingEntity blocker, DamageSource source, float blocked) {
		ShieldBlockEvent e = new ShieldBlockEvent(blocker, source, blocked);
		e.sendEvent();
		return e;
	}

	public static boolean onEffectRemoved(LivingEntity entity, Holder<MobEffect> effect, @Nullable EffectCure cure) {
		var event = new MobEffectEvent.Remove(entity, effect, cure);
		event.sendEvent();
		return event.isCanceled();
	}

	public static boolean onEffectRemoved(LivingEntity entity, MobEffectInstance effectInstance, @Nullable EffectCure cure) {
		var event = new MobEffectEvent.Remove(entity, effectInstance, cure);
		event.sendEvent();
		return event.isCanceled();
	}

	/**
	 * Checks if a mob effect can be applied to an entity by firing {@link MobEffectEvent.Applicable}.
	 *
	 * @param entity The target entity the mob effect is being applied to.
	 * @param effect The mob effect being applied.
	 * @return True if the mob effect can be applied, otherwise false.
	 */
	public static boolean canMobEffectBeApplied(LivingEntity entity, MobEffectInstance effect) {
		var event = new MobEffectEvent.Applicable(entity, effect);
		event.sendEvent();
		return event.getApplicationResult();
	}
}
