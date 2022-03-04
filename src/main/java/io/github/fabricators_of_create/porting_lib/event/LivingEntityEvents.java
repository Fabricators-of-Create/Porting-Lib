package io.github.fabricators_of_create.porting_lib.event;

import java.util.Collection;
import java.util.Objects;

import lombok.Setter;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;

public class LivingEntityEvents {
	public static final Event<ExperienceDrop> EXPERIENCE_DROP = EventFactory.createArrayBacked(ExperienceDrop.class, callbacks -> (i, player) -> {
		for (ExperienceDrop callback : callbacks) {
			return callback.onLivingEntityExperienceDrop(i, player);
		}

		return i;
	});

	public static final Event<KnockBackStrength> KNOCKBACK_STRENGTH = EventFactory.createArrayBacked(KnockBackStrength.class, callbacks -> (strength, player) -> {
		for (KnockBackStrength callback : callbacks) {
			return callback.onLivingEntityTakeKnockback(strength, player);
		}

		return strength;
	});

	/**
	 * Return PASS - ignore
	 * Return SUCCESS - update value
	 * Return FAIL - cancel attack
	 */
	public static final Event<Attack> ATTACK = EventFactory.createArrayBacked(Attack.class, callbacks -> (source, damaged, amount) -> {
		for (Attack callback : callbacks) {
			InteractionResultHolder<Float> result = callback.onAttack(source, damaged, amount);
			if (result != null) {
				if (result.getResult() != InteractionResult.PASS) {
					return result;
				}
			}
		}
		return null;
	});

	public static final Event<Drops> DROPS = EventFactory.createArrayBacked(Drops.class, callbacks -> (target, source, drops) -> {
		for (Drops callback : callbacks) {
			return callback.onLivingEntityDrops(target, source, drops);
		}

		return false;
	});

	public static final Event<Fall> FALL = EventFactory.createArrayBacked(Fall.class, callbacks -> (info) -> {
		float originalDistance = info.distance;
		float originalMult = info.damageMultiplier;
		for(Fall e : callbacks) {
			e.onFall(info);
			if (info.canceled || info.distance != originalDistance || info.damageMultiplier != originalMult) {
				return;
			}
		}
	});

	public static final Event<LootingLevel> LOOTING_LEVEL = EventFactory.createArrayBacked(LootingLevel.class, callbacks -> (source, target, level, recent) -> {
		for (LootingLevel callback : callbacks) {
			int lootingLevel = callback.modifyLootingLevel(source, target, level, recent);
			if (lootingLevel != level) {
				return lootingLevel;
			}
		}

		return level;
	});

	public static final Event<Tick> TICK = EventFactory.createArrayBacked(Tick.class, callbacks -> (entity) -> {
		for (Tick callback : callbacks) {
			callback.onLivingEntityTick(entity);
		}
	});

	public static final Event<Hurt> HURT = EventFactory.createArrayBacked(Hurt.class, callbacks -> (source, amount) -> {
		for (Hurt callback : callbacks) {
			float newAmount = callback.onHurt(source, amount);
			if (newAmount != amount) return newAmount;
		}
		return amount;
	});

	public static final Event<ActuallyHurt> ACTUALLY_HURT = EventFactory.createArrayBacked(ActuallyHurt.class, callbacks -> (source, damaged, amount) -> {
		for (ActuallyHurt callback : callbacks) {
			float newAmount = callback.onHurt(source, damaged, amount);
			if (newAmount != amount) return newAmount;
		}
		return amount;
	});

	public static final Event<Jump> JUMP = EventFactory.createArrayBacked(Jump.class, callbacks -> (entity) -> {
		for (Jump callback : callbacks) {
			callback.onLivingEntityJump(entity);
		}
	});

	@FunctionalInterface
	public interface Hurt {
		float onHurt(DamageSource source, float amount);
	}

	@FunctionalInterface
	public interface ActuallyHurt {
		float onHurt(DamageSource source, LivingEntity damaged, float amount);
	}

	@FunctionalInterface
	public interface Fall {
		void onFall(FallInfo info);

		final class FallInfo {
			public final LivingEntity entity;
			public final DamageSource source;
			public float distance;
			public float damageMultiplier;
			public boolean canceled = false;

			public FallInfo(LivingEntity entity, DamageSource source, float distance, float damageMultiplier) {
				this.entity = entity;
				this.source = source;
				this.distance = distance;
				this.damageMultiplier = damageMultiplier;
			}
		}
	}

	@FunctionalInterface
	public interface ExperienceDrop {
		int onLivingEntityExperienceDrop(int i, Player player);
	}

	@FunctionalInterface
	public interface KnockBackStrength {
		double onLivingEntityTakeKnockback(double strength, Player player);
	}

	@FunctionalInterface
	public interface Attack {
		@Nullable
		InteractionResultHolder<Float> onAttack(DamageSource source, LivingEntity damaged, float amount);
	}

	@FunctionalInterface
	public interface Drops {
		boolean onLivingEntityDrops(LivingEntity target, DamageSource source, Collection<ItemEntity> drops);
	}

	@FunctionalInterface
	public interface LootingLevel {
		int modifyLootingLevel(DamageSource source, LivingEntity target, int currentLevel, boolean recentlyHit);
	}

	@FunctionalInterface
	public interface Tick {
		void onLivingEntityTick(LivingEntity entity);
	}

	@FunctionalInterface
	public interface Jump {
		void onLivingEntityJump(LivingEntity entity);
	}
}
