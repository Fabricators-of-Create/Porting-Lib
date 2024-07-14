package io.github.fabricators_of_create.porting_lib.common.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

import java.util.function.Supplier;

/**
 * Helper class to define a custom tier
 */
public final class SimpleTier implements Tier {
	private final TagKey<Block> incorrectBlocksForDrops;
	private final int uses;
	private final float speed;
	private final float attackDamageBonus;
	private final int enchantmentValue;
	@Nonnull
	private final TagKey<Block> tag;
	@Nonnull
	private final Supplier<Ingredient> repairIngredient;

	public SimpleTier(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus, int enchantmentValue,
					  @Nonnull TagKey<Block> tag, @Nonnull Supplier<Ingredient> repairIngredient) {
		this.incorrectBlocksForDrops = incorrectBlocksForDrops;
		this.uses = uses;
		this.speed = speed;
		this.attackDamageBonus = attackDamageBonus;
		this.enchantmentValue = enchantmentValue;
		this.tag = tag;
		this.repairIngredient = repairIngredient;
	}

	@Override
	public int getUses() {
		return this.uses;
	}

	@Override
	public float getSpeed() {
		return this.speed;
	}

	@Override
	public float getAttackDamageBonus() {
		return this.attackDamageBonus;
	}

	@Override
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return this.incorrectBlocksForDrops;
	}

	@Override
	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	@Nonnull
	public TagKey<Block> getTag() {
		return this.tag;
	}

	@Nonnull
	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	@Override
	public String toString() {
		return "SimpleTier[" +
				"incorrectBlocksForDrops=" + incorrectBlocksForDrops + ", " +
				"uses=" + uses + ", " +
				"speed=" + speed + ", " +
				"attackDamageBonus=" + attackDamageBonus + ", " +
				"enchantmentValue=" + enchantmentValue + ", " +
				"tag=" + tag + ", " +
				"repairIngredient=" + repairIngredient + ']';
	}
}
