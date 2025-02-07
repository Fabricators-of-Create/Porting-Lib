package io.github.fabricators_of_create.porting_lib.util;

import java.util.Collection;
import java.util.Collections;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient.Entry;
import net.minecraft.util.registry.Registry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MultiItemValue implements Entry
{
	private Collection<ItemStack> items;
	public MultiItemValue(Collection<ItemStack> items)
	{
		this.items = Collections.unmodifiableCollection(items);
	}

	@Override
	public Collection<ItemStack> getStacks()
	{
		return items;
	}

	@Override
	public JsonObject toJson()
	{
		if (items.size() == 1)
			return toJson(items.iterator().next());

		JsonObject ret = new JsonObject();
		JsonArray array = new JsonArray();
		items.forEach(stack -> array.add(toJson(stack)));
		ret.add("items", array);
		return ret;
	}

	private JsonObject toJson(ItemStack stack)
	{
		JsonObject ret = new JsonObject();
		ret.addProperty("item", Registry.ITEM.getId(stack.getItem()).toString());
		if (stack.getCount() != 1)
			ret.addProperty("count", stack.getCount());
		if (stack.getNbt() != null)
			ret.addProperty("nbt", stack.getNbt().toString()); //TODO: Better serialization?
		return ret;
	}

}
