package io.github.fabricators_of_create.porting_lib.entity.events.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class PlayerEvents {
	/**
	 * Fired when a player is granted an advancement.
	 */
	public static final Event<AdvancementGrant> ADVANCEMENT_GRANT = EventFactory.createArrayBacked(AdvancementGrant.class, callbacks -> (player, advancement) -> {
		for (AdvancementGrant callback : callbacks)
			callback.onGrantAdvancement(player, advancement);
	});

	@FunctionalInterface
	public interface AdvancementGrant {
		void onGrantAdvancement(Player player, Advancement advancement);
	}

	/**
	 * Fired while a player breaks a block. Modifies the result of {@link Player#getDestroySpeed(BlockState)}.
	 * This event is chained; multiple listeners may modify the speed.
	 */
	public static final Event<BreakSpeed> BREAK_SPEED = EventFactory.createArrayBacked(BreakSpeed.class, callbacks -> (player, state, pos, speed) -> {
		for(BreakSpeed callback : callbacks)
			speed = callback.modifyBreakSpeed(player, state, pos, speed);
		return speed;
	});

	@FunctionalInterface
	public interface BreakSpeed {
		/**
		 * @return the modified break speed, or the original if unchanged
		 */
		float modifyBreakSpeed(Player player, BlockState state, BlockPos pos,float speed);
	}

	/**
	 * Fired when an entity is used by a player, from {@link Player#interactOn(Entity, InteractionHand)}
	 */
	public static final Event<UseEntity> USE_ENTITY = EventFactory.createArrayBacked(UseEntity.class, callbacks -> ((player, hand, target) -> {
		for (UseEntity callback : callbacks) {
			InteractionResult result = callback.onUseEntity(player, hand, target);
			if(result != null)
				return result;
		}
		return null;
	}));

	@FunctionalInterface
	public interface UseEntity {
		/**
		 * @return any non-null value to cancel the interaction and replace the result of it
		 */
		@Nullable
		InteractionResult onUseEntity(Player player, InteractionHand hand, Entity used);
	}
}
