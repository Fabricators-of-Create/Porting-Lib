package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ExplosionEvents {

	public static Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks -> ((world, explosion) -> {
		for (Start event : callbacks)
			if (event.onExplosionStart(world, explosion))
				return true;
		return false;
	}));

	public static Event<Detonate> DETONATE = EventFactory.createArrayBacked(Detonate.class, callbacks -> ((world, explosion, list, diameter) -> {
		for (Detonate event : callbacks)
			event.onDetonate(world, explosion, list, diameter);
	}));

	public static Event<Knockback> KNOCKBACK = EventFactory.createArrayBacked(Knockback.class, callbacks -> ((world, explosion, entity, velocity, blocks) -> {
		for (Knockback event : callbacks) {
			velocity = event.updateExplosionKnockback(world, explosion, entity, velocity, blocks);
		}

		return velocity;
	}));

	@FunctionalInterface
	public interface Start {
		boolean onExplosionStart(Level world, ServerExplosion explosion);
	}

	@FunctionalInterface
	public interface Detonate {
		void onDetonate(Level world, ServerExplosion explosion, List<Entity> entities, List<BlockPos> blocks);
	}

	@FunctionalInterface
	public interface Knockback {
		Vec3 updateExplosionKnockback(Level world, ServerExplosion explosion, Entity entity, Vec3 initialVelocity, List<BlockPos> blocks);
	}
}
