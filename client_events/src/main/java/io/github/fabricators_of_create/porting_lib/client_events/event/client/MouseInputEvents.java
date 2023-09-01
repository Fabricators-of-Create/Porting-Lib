package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class MouseInputEvents {
	/**
	 * Fired before a mouse button press is handled. Can be used to cancel interactions.
	 */
	public static final Event<BeforeButton> BEFORE_BUTTON = EventFactory.createArrayBacked(BeforeButton.class, callbacks -> (button, modifiers, action) -> {
		for (BeforeButton callback : callbacks) {
			if (callback.beforeButtonPress(button, modifiers, action)) {
				return true;
			}
		}
		return false;
	});

	/**
	 * Fired after a mouse button press has been handled. Can not be canceled.
	 */
	public static final Event<AfterButton> AFTER_BUTTON = EventFactory.createArrayBacked(AfterButton.class, callbacks -> (button, modifiers, action) -> {
		for (AfterButton callback : callbacks) {
			callback.afterButtonPress(button, modifiers, action);
		}
	});

	/**
	 * Fired before a scroll input is handled. Can be used to cancel interactions.
	 */
	public static final Event<BeforeScroll> BEFORE_SCROLL = EventFactory.createArrayBacked(BeforeScroll.class, callbacks -> (deltaX, deltaY) -> {
		for (BeforeScroll callback : callbacks) {
			if (callback.beforeScroll(deltaX, deltaY)) {
				return true;
			}
		}
		return false;
	});

	/**
	 * Fired after a scroll input has been handled. Can not be canceled.
	 */
	public static final Event<AfterScroll> AFTER_SCROLL = EventFactory.createArrayBacked(AfterScroll.class, callbacks -> (deltaX, deltaY) -> {
		for (AfterScroll callback : callbacks) {
			callback.afterScroll(deltaX, deltaY);
		}
	});



	public interface BeforeButton {
		/**
		 * Before a button is pressed.
		 * @param button the button pressed, see {@link GLFW} key codes
		 * @param modifiers bit flags for button modifiers, see {@link GLFW} GLFW_MOD values
		 * @param action the action on the button
		 * @return true to cancel the input, false otherwise
		 */
		boolean beforeButtonPress(int button, int modifiers, Action action);
	}

	public interface AfterButton {
		/**
		 * After a button is pressed.
		 * @param button the button pressed, see {@link GLFW} key codes
		 * @param modifiers bit flags for button modifiers, see {@link GLFW} GLFW_MOD values
		 * @param action the action on the button
		 */
		void afterButtonPress(int button, int modifiers, Action action);
	}

	public interface BeforeScroll {
		/**
		 * Before a scroll is processed.
		 * @param deltaX the amount scrolled horizontally
		 * @param deltaY the amount scrolled vertically
		 * @return true to cancel the input, false otherwise
		 */
		boolean beforeScroll(double deltaX, double deltaY);
	}

	public interface AfterScroll {
		/**
		 * After a scroll is processed.
		 * @param deltaX the amount scrolled horizontally
		 * @param deltaY the amount scrolled vertically
		 */
		void afterScroll(double deltaX, double deltaY);
	}

	public enum Action {
		RELEASE,
		PRESS,
		REPEAT;

		public static Action get(int index) {
			return switch (index) {
				case 0 -> RELEASE;
				case 1 -> PRESS;
				case 2 -> REPEAT;
				default -> throw new IllegalStateException("Unexpected value: " + index);
			};
		}
	}
}
