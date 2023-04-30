package io.github.fabricators_of_create.porting_lib.entity.events.living;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;

public interface NaturalMobSpawnCallback {
	/**
	 * Called when a mob is spawned naturally. Handled scenarios:
	 * <ul>
	 *     <li>Night-time mobs ({@link NaturalSpawner})</li>
	 *     <li>Village and Witch Hut cats ({@link CatSpawner})</li>
	 *     <li>Patrols ({@link PatrolSpawner})</li>
	 *     <li>Phantoms ({@link PhantomSpawner})</li>
	 *     <li>Village Sieges ({@link VillageSiege})</li>
	 *     <li>Wandering Traders ({@link WanderingTraderSpawner})</li>
	 * </ul>
	 */
	Event<NaturalMobSpawnCallback> EVENT = EventFactory.createArrayBacked(NaturalMobSpawnCallback.class, callbacks -> (mob, x, y, z, level, spawner, type) -> {
		for(NaturalMobSpawnCallback callback : callbacks) {
			TriState result = callback.canSpawnMob(mob, x, y, z, level, spawner, type);
			if (result != TriState.DEFAULT)
				return result;
		}
		return TriState.DEFAULT;
	});

	/**
	 * @param spawner the CustomSpawner that caused this spawn, or null if {@link NaturalSpawner}
	 * @return {@link TriState#TRUE} to allow, {@link TriState#FALSE} to disallow, or {@link TriState#DEFAULT} otherwise
	 */
	TriState canSpawnMob(Mob mob, double x, double y, double z, LevelAccessor level, @Nullable CustomSpawner spawner, MobSpawnType type);
}
