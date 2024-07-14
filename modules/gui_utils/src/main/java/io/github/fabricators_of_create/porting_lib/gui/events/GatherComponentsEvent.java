package io.github.fabricators_of_create.porting_lib.gui.events;

import com.mojang.datafixers.util.Either;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Fired when a tooltip gathers the {@link TooltipComponent}s to be rendered, before any text wrapping or processing.
 * The list of components and the maximum width of the tooltip can be modified through this event.
 *
 * <p>This event is {@linkplain CancellableEvent cancellable}.
 * If this event is cancelled, then the list of components will be empty.
 *
 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
 */
public class GatherComponentsEvent extends BaseEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback c : callbacks)
			c.onGatherComponents(event);
	});
	private final ItemStack itemStack;
	private final int screenWidth;
	private final int screenHeight;
	private final List<Either<FormattedText, TooltipComponent>> tooltipElements;
	private int maxWidth;

	@ApiStatus.Internal
	public GatherComponentsEvent(ItemStack itemStack, int screenWidth, int screenHeight, List<Either<FormattedText, TooltipComponent>> tooltipElements, int maxWidth) {
		this.itemStack = itemStack;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.tooltipElements = tooltipElements;
		this.maxWidth = maxWidth;
	}

	/**
	 * {@return the item stack which the tooltip is being rendered for, or an {@linkplain ItemStack#isEmpty() empty
	 * item stack} if there is no associated item stack}
	 */
	public ItemStack getItemStack() {
		return itemStack;
	}

	/**
	 * {@return the width of the screen}.
	 * The lines of text within the tooltip are wrapped to be within the screen width, and the tooltip box itself
	 * is moved to be within the screen width.
	 */
	public int getScreenWidth() {
		return screenWidth;
	}

	/**
	 * {@return the height of the screen}
	 * The tooltip box is moved to be within the screen height.
	 */
	public int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * {@return the modifiable list of elements to be rendered on the tooltip} These elements can be either
	 * formatted text or custom tooltip components.
	 */
	public List<Either<FormattedText, TooltipComponent>> getTooltipElements() {
		return tooltipElements;
	}

	/**
	 * {@return the maximum width of the tooltip when being rendered}
	 *
	 * <p>A value of {@code -1} means an unlimited maximum width. However, an unlimited maximum width will still
	 * be wrapped to be within the screen bounds.</p>
	 */
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * Sets the maximum width of the tooltip. Use {@code -1} for unlimited maximum width.
	 *
	 * @param maxWidth the new maximum width
	 */
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onGatherComponents(this);
	}

	public interface Callback {
		void onGatherComponents(GatherComponentsEvent event);
	}
}
