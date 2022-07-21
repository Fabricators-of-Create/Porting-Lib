package io.github.fabricators_of_create.porting_lib.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;

import java.util.Arrays;

/**
 * Ingredient that matches everything from the first ingredient that is not included in the second ingredient
 */
public class DifferenceIngredient extends AbstractIngredient {
	private final Ingredient base;
	private final Ingredient subtracted;
	private ItemStack[] filteredMatchingStacks;
	private IntList packedMatchingStacks;

	protected DifferenceIngredient(Ingredient base, Ingredient subtracted) {
		this.base = base;
		this.subtracted = subtracted;
	}

	/**
	 * Gets the difference from the two ingredients
	 *
	 * @param base       Ingredient the item must match
	 * @param subtracted Ingredient the item must not match
	 * @return Ingredient that {@code base} anything in base that is not in {@code subtracted}
	 */
	public static DifferenceIngredient of(Ingredient base, Ingredient subtracted) {
		return new DifferenceIngredient(base, subtracted);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		return base.test(stack) && !subtracted.test(stack);
	}

	@Override
	public ItemStack[] getItems() {
		if (this.filteredMatchingStacks == null)
			this.filteredMatchingStacks = Arrays.stream(base.getItems())
					.filter(stack -> !subtracted.test(stack))
					.toArray(ItemStack[]::new);
		return filteredMatchingStacks;
	}

	@Override
	public boolean isEmpty() {
		return base.isEmpty();
	}

	@Override
	public IntList getStackingIds() {
		if (this.packedMatchingStacks == null) {
			ItemStack[] matchingStacks = getItems();
			this.packedMatchingStacks = new IntArrayList(matchingStacks.length);
			for (ItemStack stack : matchingStacks)
				this.packedMatchingStacks.add(StackedContents.getStackingIndex(stack));

			this.packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
		}
		return packedMatchingStacks;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
		json.add("base", base.toJson());
		json.add("subtracted", subtracted.toJson());
		return json;
	}

	@Override
	public IngredientDeserializer getDeserializer() {
		return Serializer.INSTANCE;
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(Serializer.ID);
		base.toNetwork(buffer);
		subtracted.toNetwork(buffer);
	}

	public static class Serializer implements IngredientDeserializer {
		public static final ResourceLocation ID = new ResourceLocation("forge", "difference");
		public static final Serializer INSTANCE = new Serializer();

		@Override
		public Ingredient fromJson(JsonObject json) {
			Ingredient base = Ingredient.fromJson(json.get("base"));
			Ingredient without = Ingredient.fromJson(json.get("subtracted"));
			return new DifferenceIngredient(base, without);
		}

		@Override
		public Ingredient fromNetwork(FriendlyByteBuf buffer) {
			Ingredient base = Ingredient.fromNetwork(buffer);
			Ingredient without = Ingredient.fromNetwork(buffer);
			return new DifferenceIngredient(base, without);
		}
	}
}
