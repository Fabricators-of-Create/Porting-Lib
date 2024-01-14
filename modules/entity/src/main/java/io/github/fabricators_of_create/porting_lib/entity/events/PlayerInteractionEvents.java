package io.github.fabricators_of_create.porting_lib.entity.events;

import com.google.common.base.Preconditions;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerInteractionEvents extends PlayerEvents {
	/**
	 * This event is fired when a player left clicks while targeting a block.
	 * This event controls which of {@link Block#attack(BlockState, Level, BlockPos, Player)} and/or the item harvesting methods will be called
	 * Canceling the event will cause none of the above noted methods to be called.
	 * There are various results to this event, see the getters below.
	 * <p>
	 * Note that if the event is canceled and the player holds down left mouse, the event will continue to fire.
	 * This is due to how vanilla calls the left click handler methods.
	 * <p>
	 * Also note that creative mode directly breaks the block without running any other logic.
	 * Therefore, in creative mode, {@link #setUseBlock} and {@link #setUseItem} have no effect.
	 * <p>
	 * This event is fired after {@link AttackBlockCallback}.
	 * This event will not fire if {@link AttackBlockCallback} is canceled.
	 * Developers should use the fabric version of this event, and only use this event when needed.
	 */
	public static final Event<PlayerLeftClickBlock> LEFT_CLICK_BLOCK = EventFactory.createArrayBacked(PlayerLeftClickBlock.class, callbacks -> (event -> {
		for(PlayerLeftClickBlock e : callbacks)
			e.onLeftClickBlock(event);
	}));

	/**
	 * This event is fired on the client side when the player left clicks empty space with any ItemStack.
	 * The server is not aware of when the client left clicks empty space, you will need to tell the server yourself.
	 * This event cannot be canceled.
	 */
	public static final Event<PlayerLeftClickEmpty> LEFT_CLICK_EMPTY = EventFactory.createArrayBacked(PlayerLeftClickEmpty.class, callbacks -> (event -> {
		for(PlayerLeftClickEmpty e : callbacks)
			e.onLeftClickEmpty(event);
	}));

	private final InteractionHand hand;
	private final BlockPos pos;
	@Nullable
	private final Direction face;
	private InteractionResult cancellationResult = InteractionResult.PASS;

	private PlayerInteractionEvents(Player player, InteractionHand hand, BlockPos pos, @Nullable Direction face) {
		super(Preconditions.checkNotNull(player, "Null player in PlayerInteractEvent!"));
		this.hand = Preconditions.checkNotNull(hand, "Null hand in PlayerInteractEvent!");
		this.pos = Preconditions.checkNotNull(pos, "Null position in PlayerInteractEvent!");
		this.face = face;
	}

	public static class LeftClickBlock extends PlayerInteractionEvents {
		private Result useBlock = Result.DEFAULT;
		private Result useItem = Result.DEFAULT;

		public LeftClickBlock(Player player, BlockPos pos, Direction face) {
			super(player, InteractionHand.MAIN_HAND, pos, face);
		}

		/**
		 * @return If {@link Block#attack(BlockState, Level, BlockPos, Player)} should be called. Changing this has no effect in creative mode
		 */
		public Result getUseBlock() {
			return useBlock;
		}

		/**
		 * @return If the block should be attempted to be mined with the current item. Changing this has no effect in creative mode
		 */
		public Result getUseItem() {
			return useItem;
		}

		public void setUseBlock(Result triggerBlock) {
			this.useBlock = triggerBlock;
		}

		public void setUseItem(Result triggerItem) {
			this.useItem = triggerItem;
		}

		@Override
		public void setCanceled(boolean canceled) {
			super.setCanceled(canceled);
			if (canceled) {
				useBlock = Result.DENY;
				useItem = Result.DENY;
			}
		}
		@Override
		public void sendEvent() {
			LEFT_CLICK_BLOCK.invoker().onLeftClickBlock(this);
		}
	}

	public static class LeftClickEmpty extends PlayerInteractionEvents {

		public LeftClickEmpty(Player player) {
			super(player, InteractionHand.MAIN_HAND, player.blockPosition(), null);
		}

		@Override
		public void sendEvent() {
			LEFT_CLICK_EMPTY.invoker().onLeftClickEmpty(this);
		}
	}

	/**
	 * @return The hand involved in this interaction. Will never be null.
	 */
	@NotNull
	public InteractionHand getHand() {
		return hand;
	}

	/**
	 * @return The itemstack involved in this interaction, {@code ItemStack.EMPTY} if the hand was empty.
	 */
	@NotNull
	public ItemStack getItemStack() {
		return getEntity().getItemInHand(hand);
	}

	/**
	 * If the interaction was on an entity, will be a BlockPos centered on the entity.
	 * If the interaction was on a block, will be the position of that block.
	 * Otherwise, will be a BlockPos centered on the player.
	 * Will never be null.
	 * @return The position involved in this interaction.
	 */
	@NotNull
	public BlockPos getPos() {
		return pos;
	}

	/**
	 * @return The face involved in this interaction. For all non-block interactions, this will return null.
	 */
	@Nullable
	public Direction getFace() {
		return face;
	}

	/**
	 * @return Convenience method to get the level of this interaction.
	 */
	public Level getLevel() {
		return getEntity().level();
	}

	/**
	 * @return The InteractionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
	 * method of the event. By default, this is {@link InteractionResult#PASS}, meaning cancelled events will cause
	 * the client to keep trying more interactions until something works.
	 */
	public InteractionResult getCancellationResult() {
		return cancellationResult;
	}

	/**
	 * Set the InteractionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
	 * method of the event.
	 * Note that this only has an effect on {@link RightClickBlock}, {@link RightClickItem}, {@link EntityInteract}, and {@link EntityInteractSpecific}.
	 */
	public void setCancellationResult(InteractionResult result) {
		this.cancellationResult = result;
	}

	@FunctionalInterface
	public interface PlayerLeftClickBlock {
		void onLeftClickBlock(LeftClickBlock event);
	}

	@FunctionalInterface
	public interface PlayerLeftClickEmpty {
		void onLeftClickEmpty(LeftClickEmpty event);
	}
}
