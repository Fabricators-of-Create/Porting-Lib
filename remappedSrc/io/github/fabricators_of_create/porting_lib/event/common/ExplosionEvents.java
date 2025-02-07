package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
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

	@FunctionalInterface
	public interface Start {
		boolean onExplosionStart(World world, Explosion explosion);
	}

	@FunctionalInterface
	public interface Detonate {
		void onDetonate(World world, Explosion explosion, List<Entity> list, double diameter);
	}
}
