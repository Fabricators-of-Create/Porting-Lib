package io.github.fabricators_of_create.porting_lib.models.generators.item;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Stub class to extend for item model data providers, eliminates some
 * boilerplate constructor parameters.
 */
public abstract class ItemModelProvider extends ModelProvider<ItemModelBuilder> {

	public ItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
		super(output, modid, ITEM_FOLDER, ItemModelBuilder::new, existingFileHelper);
	}

	public ItemModelBuilder basicItem(Item item)
	{
		return basicItem(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
	}

	public ItemModelBuilder basicItem(ResourceLocation item)
	{
		return getBuilder(item.toString())
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
	}

	@NotNull
	@Override
	public String getName() {
		return "Item Models: " + modid;
	}
}
