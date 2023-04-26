package io.github.fabricators_of_create.porting_lib.util;

import java.util.List;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.GrindstoneEvents;
import io.github.fabricators_of_create.porting_lib.event.common.MobSpawnEvents;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerInteractionEvents;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockItemExtensions;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.LootModifierManager;
import io.github.fabricators_of_create.porting_lib.loot.LootTableIdCondition;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.loot.LootContext;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"removal", "UnstableApiUsage"})
public class PortingHooks {
	public static boolean isCorrectToolForDrops(@NotNull BlockState state, @NotNull Player player) {
		if (!state.requiresCorrectToolForDrops())
			return true;

		return player.hasCorrectToolForDrops(state);
	}

	public static int onBlockBreakEvent(Level world, GameType gameType, ServerPlayer entityPlayer, BlockPos pos) {
		// Logic from tryHarvestBlock for pre-canceling the event
		boolean preCancelEvent = false;
		ItemStack itemstack = entityPlayer.getMainHandItem();
		if (!itemstack.isEmpty() && !itemstack.getItem().canAttackBlock(world.getBlockState(pos), world, pos, entityPlayer)) {
			preCancelEvent = true;
		}

		if (gameType.isBlockPlacingRestricted()) {
			if (gameType == GameType.SPECTATOR)
				preCancelEvent = true;

			if (!entityPlayer.mayBuild()) {
				if (itemstack.isEmpty() || !itemstack.hasAdventureModeBreakTagForBlock(world.registryAccess().registryOrThrow(Registries.BLOCK), new BlockInWorld(world, pos, false)))
					preCancelEvent = true;
			}
		}

		// Tell client the block is gone immediately then process events
		if (world.getBlockEntity(pos) == null) {
			entityPlayer.connection.send(new ClientboundBlockUpdatePacket(pos, world.getFluidState(pos).createLegacyBlock()));
		}

		// Post the block break event
		BlockState state = world.getBlockState(pos);
		BlockEvents.BreakEvent event = new BlockEvents.BreakEvent(world, pos, state, entityPlayer);
		event.setCanceled(preCancelEvent);
		event.sendEvent();

		// Handle if the event is canceled
		if (event.isCanceled()) {
			// Let the client know the block still exists
			entityPlayer.connection.send(new ClientboundBlockUpdatePacket(world, pos));

			// Update any tile entity data for this block
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity != null) {
				Packet<?> pkt = blockEntity.getUpdatePacket();
				if (pkt != null) {
					entityPlayer.connection.send(pkt);
				}
			}
		}
		return event.isCanceled() ? -1 : event.getExpToDrop();
	}

	/**
	 * All loot table drops should be passed to this function so that mod added effects
	 * (e.g. smelting enchantments) can be processed.
	 *
	 * @param list The loot generated
	 * @param context The loot context that generated that loot
	 * @return The modified list
	 *
	 * @deprecated Use {@link #modifyLoot(ResourceLocation, ObjectArrayList, LootContext)} instead.
	 *
	 * @implNote This method will use the {@linkplain LootTableIdCondition#UNKNOWN_LOOT_TABLE
	 *           unknown loot table marker} when redirecting.
	 */
	@Deprecated
	public static List<ItemStack> modifyLoot(List<ItemStack> list, LootContext context) {
		return modifyLoot(LootTableIdCondition.UNKNOWN_LOOT_TABLE, ObjectArrayList.wrap((ItemStack[]) list.toArray()), context);
	}

	/**
	 * Handles the modification of loot table drops via the registered Global Loot Modifiers,
	 * so that custom effects can be processed.
	 *
	 * <p>All loot-table generated loot should be passed to this function.</p>
	 *
	 * @param lootTableId The ID of the loot table currently being queried
	 * @param generatedLoot The loot generated by the loot table
	 * @param context The loot context that generated the loot, unmodified
	 * @return The modified list of drops
	 *
	 * @apiNote The given context will be modified by this method to also store the ID of the
	 *          loot table being queried.
	 */
	public static ObjectArrayList<ItemStack> modifyLoot(ResourceLocation lootTableId, ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		context.setQueriedLootTableId(lootTableId); // In case the ID was set via copy constructor, this will be ignored: intended
		LootModifierManager man = LootModifierManager.getLootModifierManager();
		for (IGlobalLootModifier mod : man.getAllLootMods()) {
			generatedLoot = mod.apply(generatedLoot, context);
		}
		return generatedLoot;
	}

	public static void init() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				PartEntity<?>[] parts = partEntity.getParts();
				if (parts != null) {
					for (PartEntity<?> part : parts) {
						world.getPartEntityMap().put(part.getId(), part);
					}
				}
			}
		});
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if(entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				PartEntity<?>[] parts = partEntity.getParts();
				if (parts != null) {
					for (PartEntity<?> part : parts) {
						world.getPartEntityMap().remove(part.getId());
					}
				}
			}
		});
		EntityEvents.ON_JOIN_WORLD.register((entity, world, loadedFromDisk) -> {
			if (entity.getClass().equals(ItemEntity.class)) {
				ItemStack stack = ((ItemEntity)entity).getItem();
				Item item = stack.getItem();
				if (item.hasCustomEntity(stack)) {
					Entity newEntity = item.createEntity(world, entity, stack);
					if (newEntity != null) {
						entity.discard();
						var executor = LogicalSidedProvider.WORKQUEUE.get(world.isClientSide ? EnvType.CLIENT : EnvType.SERVER);
						executor.tell(new TickTask(0, () -> world.addFreshEntity(newEntity)));
						return false;
					}
				}
			}
			return true;
		});
		RegistryEntryRemovedCallback.event(BuiltInRegistries.ITEM).register((rawId, id, item) -> {
			if (item instanceof BlockItemExtensions blockItem) {
				blockItem.removeFromBlockToItemMap(Item.BY_BLOCK, item);
			}
		});
	}

	public static int getLootingLevel(Entity target, @Nullable Entity killer, @Nullable DamageSource cause) {
		if (killer instanceof LivingEntity)
			return EnchantmentHelper.getMobLooting((LivingEntity)killer);
		return 0;
	}

	public static int onGrindstoneChange(@NotNull ItemStack top, @NotNull ItemStack bottom, Container outputSlot, int xp) {
		GrindstoneEvents.OnplaceItem e = new GrindstoneEvents.OnplaceItem(top, bottom, xp);
		if (e.isCanceled()) {
			outputSlot.setItem(0, ItemStack.EMPTY);
			return -1;
		}
		if (e.getOutput().isEmpty()) return Integer.MIN_VALUE;

		outputSlot.setItem(0, e.getOutput());
		return e.getXp();
	}

	public static InteractionResult onInteractEntityAt(Player player, Entity entity, HitResult ray, InteractionHand hand) {
		Vec3 vec3d = ray.getLocation().subtract(entity.position());
		return onInteractEntityAt(player, entity, vec3d, hand);
	}

	public static InteractionResult onInteractEntityAt(Player player, Entity entity, Vec3 vec3d, InteractionHand hand) {
		PlayerInteractionEvents.EntityInteractSpecific evt = new PlayerInteractionEvents.EntityInteractSpecific(player, hand, entity, vec3d);
		evt.sendEvent();
		return evt.isCanceled() ? evt.getCancellationResult() : null;
	}

	public static InteractionResult onInteractEntity(Player player, Entity entity, InteractionHand hand) {
		PlayerInteractionEvents.EntityInteract evt = new PlayerInteractionEvents.EntityInteract(player, hand, entity);
		evt.sendEvent();
		return evt.isCanceled() ? evt.getCancellationResult() : null;
	}

	public static InteractionResult onItemRightClick(Player player, InteractionHand hand) {
		PlayerInteractionEvents.RightClickItem evt = new PlayerInteractionEvents.RightClickItem(player, hand);
		evt.sendEvent();
		return evt.isCanceled() ? evt.getCancellationResult() : null;
	}

	/**
	 * Vanilla calls to {@link Mob#finalizeSpawn} are replaced with calls to this method via asm.<br>
	 * Mods should call this method in place of calling {@link Mob#finalizeSpawn}. Super calls (from within overrides) should not be wrapped.
	 * <p>
	 * Returns the SpawnGroupData from this event, or null if it was canceled.
	 * @see MobSpawnEvents.FinalizeSpawn
	 * @see Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)
	 * @implNote Changes to the signature of this method must be reflected in the method redirector coremod.
	 */
	@Nullable
	public static SpawnGroupData onFinalizeSpawn(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @org.jetbrains.annotations.Nullable CompoundTag spawnTag) {
		var event = new MobSpawnEvents.FinalizeSpawn(mob, level, mob.getX(), mob.getY(), mob.getZ(), difficulty, spawnType, spawnData, spawnTag, null);
		event.sendEvent();
		boolean cancel = event.isCanceled();

		if (!cancel) {
			mob.finalizeSpawn(level, event.getDifficulty(), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag());
		}

		return cancel ? null : event.getSpawnData();
	}

	/**
	 * Returns the FinalizeSpawn event instance, or null if it was canceled.<br>
	 * This is separate since mob spawners perform special finalizeSpawn handling when NBT data is present, but we still want to fire the event.<br>
	 * This overload is also the only way to pass through a {@link BaseSpawner} instance.
	 * @see MobSpawnEvents.FinalizeSpawn
	 */
	@Nullable
	public static MobSpawnEvents.FinalizeSpawn onFinalizeSpawnSpawner(Mob mob, ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag spawnTag, BaseSpawner spawner) {
		var event = new MobSpawnEvents.FinalizeSpawn(mob, level, mob.getX(), mob.getY(), mob.getZ(), difficulty, MobSpawnType.SPAWNER, spawnData, spawnTag, spawner);
		event.sendEvent();
		return event.isCanceled() ? null : event;
	}

	public static BaseEvent.Result canEntityDespawn(Mob entity, ServerLevelAccessor level) {
		MobSpawnEvents.AllowDespawn event = new MobSpawnEvents.AllowDespawn(entity, level);
		event.sendEvent();
		return event.getResult();
	}
}
