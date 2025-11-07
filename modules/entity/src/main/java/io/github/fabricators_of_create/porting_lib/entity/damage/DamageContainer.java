package io.github.fabricators_of_create.porting_lib.entity.damage;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityInvulnerabilityCheckEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingIncomingDamageEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingShieldBlockEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * DamageContainer encapsulates aspects of the entity damage sequence so that
 * relevant context related to damage dealt is accessible throughout the entire
 * sequence.
 * <p>Note: certain values will be defaults until the stage in the sequence when they are set.</p>
 * <h3>Porting Lib changes (Fabric)</h3>
 * <br>In order to be more mod compatible with mods that don't rely on porting lib, we supply {@link DamageConflictResolver} which is only used if {@link DamageContainer#getOriginalDamage()} != vanillaDamage when applying new damage,
 * <br>indicating that some mod has modified the damage outside the scope of the damage container.
 * <br>By default, the modified damage from outside the container will be applied, and the external damage will be set as the {@link DamageContainer#newDamage} in the container.
 * <h3>The Damage Sequence</h3>
 * <ol>
 * <li>{@link LivingEntity#hurt} is invoked on the recipient from the source of
 * the attack.</li>
 * <li>{@link Entity#isInvulnerableTo} is invoked and fires {@link EntityInvulnerabilityCheckEvent}.</li>
 * <li>After determining the entity is vulnerable, the {@link DamageContainer} in instantiated for the entity.</li>
 * <li>{@link LivingIncomingDamageEvent} is fired.</li>
 * <li>{@link LivingShieldBlockEvent} fires and the result determines if shield effects apply.</li>
 * <li>{@link LivingEntity#actuallyHurt} is called.</li>
 * <li>armor, magic, and mob_effect reductions are captured in the DamageContainer.</li>
 * <li>{@link LivingDamageEvent.Pre} is fired.</li>
 * <li>absorption reductions are captured in the DamageContainer.</li>
 * <li>if the damage is not zero, entity health is modified and recorded and {@link LivingDamageEvent.Post} is fired.</li>
 * </ol>
 */
public class DamageContainer {
	public enum Reduction {
		/** Damage reduced from post attack invulnerability. */
		INVULNERABILITY,
		/** Damage reduced from the effects of armor. */
		ARMOR,
		/** Damage reduced from enchantments on armor. */
		ENCHANTMENTS,
		/** Damage reduced from active mob effects. */
		MOB_EFFECTS,
		/** Damage absorbed by absorption. */
		ABSORPTION
	}

	private final EnumMap<Reduction, List<IReductionFunction>> reductionFunctions = new EnumMap<>(Reduction.class);
	private final float originalDamage;
	private final DamageSource source;
	private float newDamage;
	private final EnumMap<Reduction, Float> reductions = new EnumMap<>(Reduction.class);
	private float blockedDamage = 0f;
	private float shieldDamage = 0;
	private int invulnerabilityTicksAfterAttack = 20;

	private DamageConflictResolver conflictResolver = (type, modifiedDamage, ctx) -> switch (type) {
		case NORMAL, SHIELD_DAMAGE -> {
			setNewDamage(modifiedDamage);
			yield modifiedDamage;
		}
		case SHIELD -> {
			shieldDamage = modifiedDamage;
			yield modifiedDamage;
		}
		case BLOCKED -> {
			blockedDamage = modifiedDamage;
			newDamage -= modifiedDamage;
			yield modifiedDamage;
		}
	};

	public DamageContainer(DamageSource source, float originalDamage) {
		this.source = source;
		this.originalDamage = originalDamage;
		this.newDamage = originalDamage;
	}

	/** {@return the value passed into {@link LivingEntity#hurt(DamageSource, float)} before any modifications are made} */
	public float getOriginalDamage() {
		return originalDamage;
	}

	/** {@return the damage source for this damage sequence} */
	public DamageSource getSource() {
		return source;
	}

	/**
	 * This sets the current damage value for the entity at the stage of the damage sequence in which it is set.
	 * Subsequent steps in the damage sequence will use and modify this value accordingly. If this is called in
	 * the final step of the sequence, this value will be applied against the entity's health.
	 *
	 * @param damage the amount to harm this entity at the end of the damage sequence
	 */
	public void setNewDamage(float damage) {
		this.newDamage = damage;
	}

	/** {@return the current amount expected to be applied to the entity or used in subsequent damage calculations} */
	public float getNewDamage() {
		return newDamage;
	}

	/**
	 * Adds a callback modifier to the vanilla damage reductions. Each function will be performed in sequence
	 * on the vanilla value at the time the {@link DamageContainer.Reduction} type is set by vanilla.
	 * <ul>
	 * <li>only the {@link LivingIncomingDamageEvent EntityPreDamageEvent}
	 * happens early enough in the sequence for this method to have any effect.</li>
	 * <li>if the vanilla reduction is not triggered, the reduction function will not execute.</li>
	 * </ul>
	 *
	 * @param type              The reduction type your function will apply to
	 * @param reductionFunction takes the current reduction from vanilla and any preceding functions and returns a new
	 *                          value for the reduction. These are always executed in insertion order.
	 */

	public void addModifier(Reduction type, IReductionFunction reductionFunction) {
		this.reductionFunctions.computeIfAbsent(type, a -> new ArrayList<>()).add(reductionFunction);
	}

	/** {@return the damage blocked during the {@link LivingShieldBlockEvent }} */
	public float getBlockedDamage() {
		return blockedDamage;
	}

	/** {@return the durability applied to the applicable shield after {@link LivingShieldBlockEvent} returned a successful block} */
	public float getShieldDamage() {
		return shieldDamage;
	}

	/**
	 * Explicitly sets the invulnerability ticks after the damage has been applied.
	 *
	 * @param ticks Ticks of invulnerability after this damage sequence
	 */
	public void setPostAttackInvulnerabilityTicks(int ticks) {
		this.invulnerabilityTicksAfterAttack = ticks;
	}

	/** {@return the number of ticks this entity will be invulnerable after damage is applied} */
	public int getPostAttackInvulnerabilityTicks() {
		return invulnerabilityTicksAfterAttack;
	}

	/**
	 * This provides a post-reduction value for the reduction and modifiers. This will always return zero
	 * before {@link LivingDamageEvent.Pre} and will consume all
	 * modifiers prior to the event.
	 *
	 * @param type the specific source type of the damage reduction
	 * @return The amount of damage reduced by armor after vanilla armor reductions and added modifiers
	 */
	public float getReduction(Reduction type) {
		return reductions.getOrDefault(type, 0f);
	}

	// Fabric only

	/**
	 * The conflict resolver is only used if the original damage in vanilla != originalDamage in the damage container, meaning an external mod has modified the damage.
	 * By default, the modified external damage will be applied and {@link DamageContainer#getNewDamage()} will be ignored.
	 * Setting the conflict resolver can be used to change this behavior.
	 * @param conflictResolver The damage resolver to use.
	 */
	public void setConflictResolver(DamageConflictResolver conflictResolver) {
		this.conflictResolver = conflictResolver;
	}

	/**
	 * @return The damage resolver to use see {@link DamageContainer#setConflictResolver(DamageConflictResolver)} and {@link DamageContainer}
	 */
	public DamageConflictResolver getConflictResolver() {
		return this.conflictResolver;
	}

	//=============INTERNAL METHODS - DO NOT USE===================

	@ApiStatus.Internal
	public void setBlockedDamage(LivingShieldBlockEvent event) {
		if (event.getBlocked()) {
			this.blockedDamage = event.getBlockedDamage();
			this.shieldDamage = event.shieldDamage();
			this.newDamage -= this.blockedDamage;
		}
	}

	@ApiStatus.Internal
	public void setReduction(Reduction reduction, float amount) {
		float modifiedReduction = modifyReduction(reduction, amount);
		this.reductions.put(reduction, modifiedReduction);
		this.newDamage -= modifiedReduction;
	}

	private float modifyReduction(Reduction type, float reduction) {
		for (var func : reductionFunctions.getOrDefault(type, List.of())) {
			reduction = func.modify(this, reduction);
		}
		return reduction;
	}

	public enum DamageConflictType {
		NORMAL,
		BLOCKED,
		SHIELD,
		SHIELD_DAMAGE
	}

	@FunctionalInterface
	public interface DamageConflictResolver {
		float resolve(DamageConflictType type, float modifiedDamage, @Nullable Object context);
	}
}
