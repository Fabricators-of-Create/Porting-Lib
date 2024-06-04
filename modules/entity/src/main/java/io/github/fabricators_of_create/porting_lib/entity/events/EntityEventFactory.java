package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingDamageEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class EntityEventFactory {
	public static boolean onProjectileImpact(Projectile projectile, HitResult ray) {
		ProjectileImpactEvent event = new ProjectileImpactEvent(projectile, ray);
		event.sendEvent();
		return event.isCanceled();
	}

	// This event is probably not going to be implemented but is here for possible future compatibility
	public static boolean getMobGriefingEvent(Level level, @Nullable Entity entity) {
//		if (entity == null)
		return level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);

//		EntityMobGriefingEvent event = new EntityMobGriefingEvent(entity);
//		MinecraftForge.EVENT_BUS.post(event);
//
//		Result result = event.getResult();
//		return result == Result.DEFAULT ? level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) : result == Result.ALLOW;
	}

	public static float onLivingHurt(LivingEntity entity, DamageSource src, float amount) {
		LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
		event.sendEvent();
		return (event.isCanceled() ? 0 : event.getAmount());
	}

	public static float onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
		LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
		event.sendEvent();
		return (event.isCanceled() ? 0 : event.getAmount());
	}

	public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
		LivingDeathEvent event = new LivingDeathEvent(entity, src);
		event.sendEvent();
		return event.isCanceled();
	}

	public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
		return LivingEntityEvents.DROPS.invoker().onLivingEntityDrops(entity, source, drops, lootingLevel, recentlyHit);
	}

	public static void onLivingJump(LivingEntity entity) {
		new LivingEntityEvents.LivingJumpEvent(entity).sendEvent();
	}
}
