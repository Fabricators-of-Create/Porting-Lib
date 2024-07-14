package io.github.fabricators_of_create.porting_lib.entity.events.player;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * PlayerDestroyItemEvent is fired when a player destroys an item.<br>
 * This event is fired whenever a player destroys an item in
 * {@link MultiPlayerGameMode#destroyBlock(BlockPos)},
 * {@link MultiPlayerGameMode#useItem(Player, InteractionHand)},
 * {@link MultiPlayerGameMode#useItemOn(LocalPlayer, InteractionHand, BlockHitResult)} ,
 * {@link Player#attack(Entity)},
 * {@code Player#hurtCurrentlyUsedShield(float)},
 * {@link Player#interactOn(Entity, InteractionHand)},
 * {@link ServerPlayerGameMode#useItem(ServerPlayer, Level, ItemStack, InteractionHand)} ,
 * {@link ServerPlayerGameMode#useItemOn(ServerPlayer, Level, ItemStack, InteractionHand, BlockHitResult)}
 * and {@link ServerPlayerGameMode#destroyBlock(BlockPos)}.<br>
 * <br>
 * {@link #original} contains the original ItemStack before the item was destroyed. <br>
 * (@link #hand) contains the hand that the current item was held in.<br>
 * <br>
 * This event is not {@link CancellableEvent}.<br>
 * <br>
 * This event is fired from {@link EntityHooks#onPlayerDestroyItem(Player, ItemStack, InteractionHand)}.<br>
 **/
public class PlayerDestroyItemEvent extends PlayerEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks)
			callback.onPlayerDestroyItem(event);
	});

	private final ItemStack original;
	@Nullable
	private final InteractionHand hand; // May be null if this player destroys the item by any use besides holding it.

	public PlayerDestroyItemEvent(Player player, ItemStack original, @Nullable InteractionHand hand) {
		super(player);
		this.original = original;
		this.hand = hand;
	}

	public ItemStack getOriginal() {
		return this.original;
	}

	@Nullable
	public InteractionHand getHand() {
		return this.hand;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onPlayerDestroyItem(this);
	}

	public interface Callback {
		void onPlayerDestroyItem(PlayerDestroyItemEvent event);
	}
}
