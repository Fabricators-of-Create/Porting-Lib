package io.github.fabricators_of_create.porting_lib.crafting;

import com.google.gson.JsonElement;

import io.github.tropheusj.serialization_hooks.ingredient.BaseCustomIngredient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * Extension of {@link Ingredient} which makes most methods custom ingredients need to implement abstract, and removes the static constructors
 * Mods are encouraged to extend this class for their custom ingredients
 */
public abstract class AbstractIngredient extends BaseCustomIngredient {
	/**
	 * Empty constructor, for the sake of dynamic ingredients
	 */
	protected AbstractIngredient() {
		super(Stream.of());
	}

	/**
	 * Value constructor, for ingredients that have some vanilla representation
	 */
	protected AbstractIngredient(Stream<? extends Entry> values) {
		super(values);
	}

	@Override
	public abstract JsonElement toJson();

	/* Hide vanilla ingredient static constructors to reduce errors with constructing custom ingredients */

	/**
	 * @deprecated use {@link Ingredient#ofEntries(Stream)}
	 */
	@Deprecated
	public static Ingredient ofEntries(Stream<? extends Ingredient.Entry> values) {
		throw new UnsupportedOperationException("Use Ingredient.fromValues()");
	}

	/**
	 * @deprecated use {@link Ingredient#empty()}
	 */
	@Deprecated
	public static Ingredient empty() {
		throw new UnsupportedOperationException("Use Ingredient.of()");
	}

	/**
	 * @deprecated use {@link Ingredient#ofItems(ItemConvertible...)} (Stream)}
	 */
	@Deprecated
	public static Ingredient ofItems(ItemConvertible... items) {
		throw new UnsupportedOperationException("Use Ingredient.of()");
	}

	/**
	 * @deprecated use {@link Ingredient#ofStacks(ItemStack...)} (Stream)}
	 */
	@Deprecated
	public static Ingredient ofStacks(ItemStack... stacks) {
		throw new UnsupportedOperationException("Use Ingredient.of()");
	}

	/**
	 * @deprecated use {@link Ingredient#ofStacks(Stream)} (Stream)}
	 */
	@Deprecated
	public static Ingredient ofStacks(Stream<ItemStack> stacks) {
		throw new UnsupportedOperationException("Use Ingredient.of()");
	}

	/**
	 * @deprecated use {@link Ingredient#fromTag(TagKey)} (Stream)}
	 */
	@Deprecated
	public static Ingredient fromTag(TagKey<Item> tag) {
		throw new UnsupportedOperationException("Use Ingredient.of()");
	}

	/**
	 * @deprecated use {@link Ingredient#fromPacket(PacketByteBuf)}
	 */
	@Deprecated
	public static Ingredient fromPacket(PacketByteBuf buffer) {
		throw new UnsupportedOperationException("Use Ingredient.fromNetwork()");
	}

	/**
	 * @deprecated use {@link Ingredient#fromJson(JsonElement)} (Stream)}
	 */
	@Deprecated
	public static Ingredient fromJson(@Nullable JsonElement json) {
		throw new UnsupportedOperationException("Use Ingredient.fromJson()");
	}
}
