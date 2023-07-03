package io.github.fabricators_of_create.porting_lib.entity.events.living;

import javax.annotation.Nonnull;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.EquipmentChange;
import net.fabricmc.fabric.api.util.TriState;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;

public class LivingEntityEvents {
	public static final Event<Tick> TICK = EventFactory.createArrayBacked(Tick.class, callbacks -> (entity) -> {
		for (Tick callback : callbacks) {
			callback.onLivingEntityTick(entity);
		}
	});

	@FunctionalInterface
	public interface Tick {
		void onLivingEntityTick(LivingEntity entity);
	}

	public static final Event<Jump> JUMP = EventFactory.createArrayBacked(Jump.class, callbacks -> (entity) -> {
		for (Jump callback : callbacks) {
			callback.onLivingEntityJump(entity);
		}
	});

	@FunctionalInterface
	public interface Jump {
		void onLivingEntityJump(LivingEntity entity);
	}

	public static final Event<Visibility> VISIBILITY = EventFactory.createArrayBacked(Visibility.class, callbacks -> (entity, lookingEntity, originalMultiplier) -> {
		for (Visibility e : callbacks) {
			double newMultiplier = e.getEntityVisibilityMultiplier(entity, lookingEntity, originalMultiplier);
			if (newMultiplier != originalMultiplier)
				return newMultiplier;
		}
		return originalMultiplier;
	});

	@FunctionalInterface
	public interface Visibility {
		double getEntityVisibilityMultiplier(LivingEntity entity, Entity lookingEntity, double originalMultiplier);
	}

	/**
	 * Fired when a living entity finishes using an item. Examples:
	 * <ul>
	 *     <li>Drawing a crossbow</li>
	 *     <li>Eating food</li>
	 *     <li>Drinking a potion</li>
	 * </ul>
	 */
	public static final Event<FinishUsingItem> FINISH_USING_ITEM = EventFactory.createArrayBacked(FinishUsingItem.class, callbacks -> (entity, original, used) -> {
		for (FinishUsingItem callback : callbacks) {
			ItemStack result = callback.modifyUseResult(entity, original, used);
			if (result != null)
				return result;
		}
		return null;
	});

	@FunctionalInterface
	public interface FinishUsingItem {
		/**
		 * @return a modified use result, or null if unchanged
		 */
		@Nullable
		ItemStack modifyUseResult(LivingEntity entity, ItemStack original, ItemStack used);
	}

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
	public static final Event<NaturalSpawn> NATURAL_SPAWN = EventFactory.createArrayBacked(NaturalSpawn.class, callbacks -> (mob, x, y, z, level, spawner, type) -> {
		for(NaturalSpawn callback : callbacks) {
			TriState result = callback.canSpawnMob(mob, x, y, z, level, spawner, type);
			if (result != TriState.DEFAULT)
				return result;
		}
		return TriState.DEFAULT;
	});

	@FunctionalInterface
	public interface NaturalSpawn {
		/**
		 * @param spawner the {@link CustomSpawner} that caused this spawn, or null if {@link NaturalSpawner}
		 * @return {@link TriState#TRUE} to allow, {@link TriState#FALSE} to disallow, or {@link TriState#DEFAULT} otherwise
		 */
		TriState canSpawnMob(Mob mob, double x, double y, double z, LevelAccessor level, @Nullable CustomSpawner spawner, MobSpawnType type);
	}

	/**
	 * Fired when a {@link Mob} sets its target.
	 * Calling {@link Mob#setTarget(LivingEntity)} from here will cause this event to be invoked again.
	 * Beware of infinite loops!
	 */
	public static final Event<SetTarget> SET_TARGET = EventFactory.createArrayBacked(SetTarget.class, callbacks -> (targeting, target) -> {
		for (SetTarget callback : callbacks) {
			callback.onMobEntitySetTarget(targeting, target);
		}
	});

	@FunctionalInterface
	public interface SetTarget {
		void onMobEntitySetTarget(Mob targeting, @Nullable LivingEntity target);
	}
}
