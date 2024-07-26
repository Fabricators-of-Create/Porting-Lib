package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;

import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import net.fabricmc.api.EnvType;
import net.minecraft.server.TickTask;
import net.minecraft.world.entity.item.ItemEntity;

import net.minecraft.world.item.context.UseOnContext;

import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import io.github.fabricators_of_create.porting_lib.event.common.GrindstoneEvents;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockItemExtensions;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import org.jetbrains.annotations.Nullable;

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

	public static void init() {
		EntityEvents.ON_JOIN_WORLD.register((entity, world, loadedFromDisk) -> {
			if (entity.getClass().equals(ItemEntity.class)) {
				ItemStack stack = ((ItemEntity) entity).getItem();
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

	/**
	 * @return -1 to cancel, MIN_VALUE to follow vanilla logic, any other number to modify granted exp
	 */
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

	@Nullable
	public static BlockState onToolUse(BlockState originalState, UseOnContext context, ToolAction toolAction, boolean simulate) {
		BlockEvents.BlockToolModificationEvent event = new BlockEvents.BlockToolModificationEvent(originalState, context, toolAction, simulate);
		return event.post() ? null : event.getFinalState();
	}
}
