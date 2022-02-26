package io.github.fabricators_of_create.porting_lib.event;

import java.util.Collection;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;

public class LivingEntityEvents {
	public static final Event<ExperienceDrop> EXPERIENCE_DROP = EventFactory.createArrayBacked(ExperienceDrop.class, callbacks -> (amount, player) -> {
		for (ExperienceDrop callback : callbacks) {
			int newAmount = callback.onLivingEntityExperienceDrop(amount, player);
			if (newAmount != amount) return newAmount;
		}

		return amount;
	});

	public static final Event<KnockBackStrength> KNOCKBACK_STRENGTH = EventFactory.createArrayBacked(KnockBackStrength.class, callbacks -> (strength, player) -> {
		for (KnockBackStrength callback : callbacks) {
			double newStrength = callback.onLivingEntityTakeKnockback(strength, player);
			if (newStrength != strength) return newStrength;
		}

		return strength;
	});

	public static final Event<Drops> DROPS = EventFactory.createArrayBacked(Drops.class, callbacks -> (source, drops) -> {
		for (Drops callback : callbacks) {
			if (callback.onLivingEntityDrops(source, drops)) return true;
		}

		return false;
	});

	public static final Event<LootingLevel> LOOTING_LEVEL = EventFactory.createArrayBacked(LootingLevel.class, callbacks -> (source, target, level) -> {
		for (LootingLevel callback : callbacks) {
			int lootingLevel = callback.modifyLootingLevel(source, target, level);
			if (lootingLevel != level) {
				return lootingLevel;
			}
		}

		return 0;
	});

	public static final Event<Tick> TICK = EventFactory.createArrayBacked(Tick.class, callbacks -> (entity) -> {
		for (Tick callback : callbacks) {
			callback.onLivingEntityTick(entity);
		}
	});

	public static final Event<Hurt> HURT = EventFactory.createArrayBacked(Hurt.class, callbacks -> (source, damaged, amount) -> {
		for (Hurt callback : callbacks) {
			float newAmount = callback.onHurt(source, damaged, amount);
			if (newAmount != amount) return newAmount;
		}
		return amount;
	});

	public static final Event<Hurt> ACTUALLY_HURT = EventFactory.createArrayBacked(Hurt.class, callbacks -> (source, damaged, amount) -> {
		for (Hurt callback : callbacks) {
			float newAmount = callback.onHurt(source, damaged, amount);
			if (newAmount != amount) return newAmount;
		}
		return amount;
	});

	public static final Event<Visibility> VISIBILITY = EventFactory.createArrayBacked(Visibility.class, callbacks -> (entity, looking, current) -> {
		for (Visibility callback : callbacks) {
			double newAmount = callback.modifyVisibility(entity, looking, current);
			if (newAmount != current) return newAmount;
		}
		return current;
	});

	@FunctionalInterface
	public interface Visibility {
		double modifyVisibility(LivingEntity entity, @Nullable Entity looking, double current);
	}

	@FunctionalInterface
	public interface Hurt {
		float onHurt(DamageSource source, LivingEntity damaged, float amount);
	}

	@FunctionalInterface
	public interface ExperienceDrop {
		int onLivingEntityExperienceDrop(int amount, Player player);
	}

	@FunctionalInterface
	public interface KnockBackStrength {
		double onLivingEntityTakeKnockback(double strength, Player player);
	}

	@FunctionalInterface
	public interface Drops {
		boolean onLivingEntityDrops(DamageSource source, Collection<ItemEntity> drops);
	}

	@FunctionalInterface
	public interface LootingLevel {
		int modifyLootingLevel(DamageSource source, LivingEntity target, int currentLevel);
	}

	@FunctionalInterface
	public interface Tick {
		void onLivingEntityTick(LivingEntity entity);
	}
}
