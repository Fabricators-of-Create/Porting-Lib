package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.HitResult;

/**
 * Events related to key player inputs.
 * All these events are tied more to the related input than the actual action.
 * This means that they will be fired regardless of the result, ex. when attacking air.
 */
public class InteractEvents {
	/**
	 * An Event fired before the player attempts to use something. Bound to right-click by default.
	 */
	public static final Event<Use> USE = EventFactory.createArrayBacked(Use.class, callbacks -> (mc, hit, hand) -> {
		for (Use callback : callbacks) {
			InteractionResult result = callback.onUse(mc, hit, hand);
			if (result != InteractionResult.PASS) {
				return result;
			}
		}
		return InteractionResult.PASS;
	});

	/**
	 * An Event fired before the player attempts to start or continue an attack. Bound to left-click by default.
	 */
	public static final Event<Attack> ATTACK = EventFactory.createArrayBacked(Attack.class, callbacks -> (mc, hit) -> {
		for (Attack callback : callbacks) {
			InteractionResult result = callback.onAttack(mc, hit);
			if (result != InteractionResult.PASS) {
				return result;
			}
		}
		return InteractionResult.PASS;
	});

	/**
	 * An Event fired before the player attempts to pick something. Bound to middle-click by default.
	 */
	public static final Event<Pick> PICK = EventFactory.createArrayBacked(Pick.class, callbacks -> (mc, hit) -> {
		for (Pick callback : callbacks) {
			if (callback.onPick(mc, hit)) {
				return true;
			}
		}
		return false;
	});

	public interface Use {
		/**
		 * When the player attempts to use something.
		 * @param hit the current targeted HitResult
		 * @param hand the hand being used
		 * @return an InteractionResult. PASS to do nothing, SUCCESS to cancel and swing hand, FAIL to cancel and do nothing.
		 */
		InteractionResult onUse(Minecraft mc, HitResult hit, InteractionHand hand);
	}

	public interface Attack {
		/**
		 * When the player attempts to start or continue an attack.
		 * @param hit the current targeted HitResult
		 * @return an InteractionResult. PASS to do nothing, SUCCESS to cancel and swing hand, FAIL to cancel and do nothing.
		 */
		InteractionResult onAttack(Minecraft mc, HitResult hit);
	}

	public interface Pick {
		/**
		 * When the player attempts to pick something.
		 * @param hit the current targeted HitResult
		 * @return true to cancel the pick, false otherwise
		 */
		boolean onPick(Minecraft mc, HitResult hit);
	}
}
