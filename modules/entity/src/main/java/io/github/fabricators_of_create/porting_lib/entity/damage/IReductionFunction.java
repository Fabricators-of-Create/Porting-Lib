package io.github.fabricators_of_create.porting_lib.entity.damage;

/**
 * An {@link IReductionFunction} is used by {@link DamageContainer} instances.<br>
 * This allows sequential modification of damage reduction values to be stored and
 * later invoked before actual reductions are applied to the damage sequence.
 */
@FunctionalInterface
public interface IReductionFunction {
	/**
	 * Consumes an existing reduction value and produces a modified value.
	 *
	 * @param container   the {@link DamageContainer} representing the damage sequence
	 *                    values for the reduction being modified
	 * @param reductionIn the initial or preceding reduction value to this operation
	 * @return the new reduction value
	 */
	float modify(DamageContainer container, float reductionIn);
}
