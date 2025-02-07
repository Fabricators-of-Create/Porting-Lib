package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Helper class to define a custom tier
 */
@SuppressWarnings("ClassCanBeRecord") // can't make it a record because the method names will be obfuscated
public final class LazyTier implements ToolMaterial {
	private final int level;
	private final int uses;
	private final float speed;
	private final float attackDamageBonus;
	private final int enchantmentValue;
	@NotNull
	private final TagKey<Block> tag;
	@NotNull
	private final Supplier<Ingredient> repairIngredient;

	public LazyTier(int level, int uses, float speed, float attackDamageBonus, int enchantmentValue,
					@NotNull TagKey<Block> tag, @NotNull Supplier<Ingredient> repairIngredient) {
		this.level = level;
		this.uses = uses;
		this.speed = speed;
		this.attackDamageBonus = attackDamageBonus;
		this.enchantmentValue = enchantmentValue;
		this.tag = tag;
		this.repairIngredient = repairIngredient;
	}

	@Override
	public int getDurability() {
		return this.uses;
	}

	@Override
	public float getMiningSpeedMultiplier() {
		return this.speed;
	}

	@Override
	public float getAttackDamage() {
		return this.attackDamageBonus;
	}

	@Override
	public int getMiningLevel() {
		return this.level;
	}

	@Override
	public int getEnchantability() {
		return this.enchantmentValue;
	}

	@NotNull
	public TagKey<Block> getTag() {
		return this.tag;
	}

	@NotNull
	@Override
	public Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	@Override
	public String toString() {
		return "DefaultTier[" +
				"level=" + level + ", " +
				"uses=" + uses + ", " +
				"speed=" + speed + ", " +
				"attackDamageBonus=" + attackDamageBonus + ", " +
				"enchantmentValue=" + enchantmentValue + ", " +
				"tag=" + tag + ", " +
				"repairIngredient=" + repairIngredient + ']';
	}
}
