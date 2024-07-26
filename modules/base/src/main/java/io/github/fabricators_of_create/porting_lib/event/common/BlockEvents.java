package io.github.fabricators_of_create.porting_lib.event.common;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public abstract class BlockEvents extends BaseEvent {

	public static final Event<BlockBreak> BLOCK_BREAK = EventFactory.createArrayBacked(BlockBreak.class, callbacks -> event -> {
		for (BlockBreak e : callbacks)
			e.onBlockBreak(event);
	});

	public static final Event<NotifyNeighbors> NEIGHBORS_NOTIFY = EventFactory.createArrayBacked(NotifyNeighbors.class, callbacks -> event -> {
		for (NotifyNeighbors e : callbacks)
			e.onNotifyNeighbors(event);
	});

	public static final Event<BlockModification> BLOCK_MODIFICATION = EventFactory.createArrayBacked(BlockModification.class, callbacks -> event -> {
		for (BlockModification e : callbacks)
			e.onBlockModification(event);
	});

	@FunctionalInterface
	public interface BlockBreak {
		void onBlockBreak(BreakEvent event);
	}

	@FunctionalInterface
	public interface NotifyNeighbors {
		void onNotifyNeighbors(NeighborNotifyEvent event);
	}

	@FunctionalInterface
	public interface BlockModification {
		void onBlockModification(BlockToolModificationEvent event);
	}

	/**
	 * Invoked before a block is placed from the head of {@link BlockItem#useOn(UseOnContext)}. Called on both client and server.
	 * Return null to fall back to further processing. Any non-null value will result in placement being cancelled.
	 */
	public static final Event<BeforePlace> BEFORE_PLACE = EventFactory.createArrayBacked(BeforePlace.class, callbacks -> context -> {
		for (BeforePlace callback : callbacks) {
			InteractionResult result = callback.beforePlace(context);
			if (result != null)
				return result;
		}
		return null;
	});

	public interface BeforePlace {
		@Nullable
		InteractionResult beforePlace(BlockPlaceContext ctx);
	}

	/**
	 * Invoked after a block is placed, from {@link BlockItem#useOn(UseOnContext)}. Called on both client and server.
	 *
	 * @deprecated Use {@link BlockEvents#POST_PROCESS_PLACE} instead.
	 */
	@Deprecated
	public static final Event<AfterPlace> AFTER_PLACE = EventFactory.createArrayBacked(AfterPlace.class, callbacks -> context -> {
		for (AfterPlace callback : callbacks)
			callback.afterPlace(context);
	});

	public interface AfterPlace {
		void afterPlace(BlockPlaceContext ctx);
	}

	/**
	 * Invoked after a block is placed, from the TAIL of {@link BlockItem#place(BlockPlaceContext)}.
	 * Called on both client and server.
	 * Provides the block's Position and BlockState as well.
	 */
	public static final Event<PostProcessPlace> POST_PROCESS_PLACE = EventFactory.createArrayBacked(PostProcessPlace.class, callbacks -> (context, blockPos, blockState) -> {
		for (PostProcessPlace callback : callbacks)
			callback.postProcessPlace(context, blockPos, blockState);
	});

	public interface PostProcessPlace {
		void postProcessPlace(BlockPlaceContext ctx, BlockPos blockPos, BlockState blockState);
	}

	private final LevelAccessor level;
	private final BlockPos pos;
	private final BlockState state;

	public BlockEvents(LevelAccessor world, BlockPos pos, BlockState state) {
		this.pos = pos;
		this.level = world;
		this.state = state;
	}

	public LevelAccessor getLevel() {
		return level;
	}

	@Deprecated(forRemoval = true)
	public LevelAccessor getWorld() {
		return level;
	}

	public BlockPos getPos() {
		return pos;
	}

	public BlockState getState() {
		return state;
	}

	public static class BreakEvent extends BlockEvents {
		/**
		 * Reference to the Player who broke the block. If no player is available, use a EntityFakePlayer
		 */
		private final Player player;
		private int exp;

		public BreakEvent(Level world, BlockPos pos, BlockState state, Player player) {
			super(world, pos, state);
			this.player = player;

			if (state == null || !PortingHooks.isCorrectToolForDrops(state, player)) { // Handle empty block or player unable to break block scenario
				this.exp = 0;
			} else {
				int bonusLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player.getMainHandItem());
				int silklevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem());
				this.exp = state.getBlock() instanceof CustomExpBlock exp ? exp.getExpDrop(state, world, world.getRandom(), pos, bonusLevel, silklevel) : 0;
			}
		}

		public Player getPlayer() {
			return player;
		}

		/**
		 * Get the experience dropped by the block after the event has processed
		 *
		 * @return The experience to drop or 0 if the event was canceled
		 */
		public int getExpToDrop() {
			return this.isCanceled() ? 0 : exp;
		}

		/**
		 * Set the amount of experience dropped by the block after the event has processed
		 *
		 * @param exp 1 or higher to drop experience, else nothing will drop
		 */
		public void setExpToDrop(int exp) {
			this.exp = exp;
		}

		@Override
		public void sendEvent() {
			BLOCK_BREAK.invoker().onBlockBreak(this);
		}
	}

	/**
	 * Fired when a physics update occurs on a block. This event acts as
	 * a way for mods to detect physics updates, in the same way a BUD switch
	 * does. This event is only called on the server.
	 */
	public static class NeighborNotifyEvent extends BlockEvents {
		private final EnumSet<Direction> notifiedSides;
		private final boolean forceRedstoneUpdate;

		public NeighborNotifyEvent(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
			super(level, pos, state);
			this.notifiedSides = notifiedSides;
			this.forceRedstoneUpdate = forceRedstoneUpdate;
		}

		/**
		 * Gets a list of directions from the base block that updates will occur upon.
		 *
		 * @return list of notified directions
		 */
		public EnumSet<Direction> getNotifiedSides() {
			return notifiedSides;
		}

		/**
		 * Get if redstone update was forced during setBlock call (0x16 to flags)
		 *
		 * @return if the flag was set
		 */
		public boolean getForceRedstoneUpdate() {
			return forceRedstoneUpdate;
		}

		@Override
		public void sendEvent() {
			NEIGHBORS_NOTIFY.invoker().onNotifyNeighbors(this);
		}
	}

	/**
	 * Note for this event isn't fully implemented on 1.20.1
	 * Fired when a block is right-clicked by a tool to change its state.
	 * For example: Used to determine if {@link ToolActions#AXE_STRIP an axe can strip},
	 * {@link ToolActions#SHOVEL_FLATTEN a shovel can path}, or {@link ToolActions#HOE_TILL a hoe can till}.
	 * <p>
	 * Care must be taken to ensure level-modifying events are only performed if {@link #isSimulated()} returns {@code false}.
	 * <p>
	 * This event is cancelable. If canceled, this will prevent the tool
	 * from changing the block's state.
	 */
	public static class BlockToolModificationEvent extends BlockEvents {
		private final UseOnContext context;
		private final ToolAction toolAction;
		private final boolean simulate;
		private BlockState state;

		public BlockToolModificationEvent(BlockState originalState, @NotNull UseOnContext context, ToolAction toolAction, boolean simulate) {
			super(context.getLevel(), context.getClickedPos(), originalState);
			this.context = context;
			this.state = originalState;
			this.toolAction = toolAction;
			this.simulate = simulate;
		}

		/**
		 * @return the player using the tool.
		 * May be null based on what was provided by {@link #getContext() the use on context}.
		 */
		@Nullable
		public Player getPlayer() {
			return this.context.getPlayer();
		}

		/**
		 * @return the tool being used
		 */
		public ItemStack getHeldItemStack() {
			return this.context.getItemInHand();
		}

		/**
		 * @return the action being performed
		 */
		public ToolAction getToolAction() {
			return this.toolAction;
		}

		/**
		 * Returns {@code true} if this event should not perform any actions that modify the level.
		 * If {@code false}, then level-modifying actions can be performed.
		 *
		 * @return {@code true} if this event should not perform any actions that modify the level.
		 * If {@code false}, then level-modifying actions can be performed.
		 */
		public boolean isSimulated() {
			return this.simulate;
		}

		/**
		 * Returns the nonnull use on context that this event was performed in.
		 *
		 * @return the nonnull use on context that this event was performed in
		 */
		@NotNull
		public UseOnContext getContext() {
			return context;
		}

		/**
		 * Sets the state to transform the block into after tool use.
		 *
		 * @param finalState the state to transform the block into after tool use
		 * @see #getFinalState()
		 */
		public void setFinalState(@Nullable BlockState finalState) {
			this.state = finalState;
		}

		/**
		 * Returns the state to transform the block into after tool use.
		 * If {@link #setFinalState(BlockState)} is not called, this will return the original state.
		 * If {@link #isCanceled()} is {@code true}, this value will be ignored and the tool action will be canceled.
		 *
		 * @return the state to transform the block into after tool use
		 */
		public BlockState getFinalState() {
			return state;
		}

		@Override
		public void sendEvent() {
			BLOCK_MODIFICATION.invoker().onBlockModification(this);
		}
	}
}
