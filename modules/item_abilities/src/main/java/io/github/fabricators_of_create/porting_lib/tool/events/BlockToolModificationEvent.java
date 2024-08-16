package io.github.fabricators_of_create.porting_lib.tool.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.level.events.BlockEvent;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

/**
 * Fired when a block is right-clicked by a tool to change its state.
 * For example: Used to determine if {@link ItemAbilities#AXE_STRIP an axe can strip},
 * {@link ItemAbilities#SHOVEL_FLATTEN a shovel can path}, or {@link ItemAbilities#HOE_TILL a hoe can till}.
 * <p>
 * Care must be taken to ensure level-modifying events are only performed if {@link #isSimulated()} returns {@code false}.
 * <p>
 * This event is {@link CancellableEvent}. If canceled, this will prevent the tool
 * from changing the block's state.
 */
public class BlockToolModificationEvent extends BlockEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks)
			callback.onBlockToolModification(event);
	});

	private final UseOnContext context;
	private final ItemAbility itemAbility;
	private final boolean simulate;
	private BlockState state;

	public BlockToolModificationEvent(BlockState originalState, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		super(context.getLevel(), context.getClickedPos(), originalState);
		this.context = context;
		this.state = originalState;
		this.itemAbility = itemAbility;
		this.simulate = simulate;
	}

	/**
	 * @return the player using the tool.
	 *         May be null based on what was provided by {@link #getContext() the use on context}.
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
	 * @return the ability being performed
	 */
	public ItemAbility getItemAbility() {
		return this.itemAbility;
	}

	/**
	 * Returns {@code true} if this event should not perform any actions that modify the level.
	 * If {@code false}, then level-modifying actions can be performed.
	 *
	 * @return {@code true} if this event should not perform any actions that modify the level.
	 *         If {@code false}, then level-modifying actions can be performed.
	 */
	public boolean isSimulated() {
		return this.simulate;
	}

	/**
	 * Returns the nonnull use on context that this event was performed in.
	 *
	 * @return the nonnull use on context that this event was performed in
	 */
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
	 * Returns the state to transform the block into after item ability use.
	 * If {@link #setFinalState(BlockState)} is not called, this will return the original state.
	 * If {@link #isCanceled()} is {@code true}, this value will be ignored and the item ability will be canceled.
	 *
	 * @return the state to transform the block into after tool use
	 */
	public BlockState getFinalState() {
		return state;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onBlockToolModification(this);
	}

	public interface Callback {
		void onBlockToolModification(BlockToolModificationEvent event);
	}
}
