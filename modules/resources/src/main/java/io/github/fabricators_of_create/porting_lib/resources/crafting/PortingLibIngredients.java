package io.github.fabricators_of_create.porting_lib.resources.crafting;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.core.util.PortingLibExtraCodecs;
import io.github.fabricators_of_create.porting_lib.registry.RegistryBuilder;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.CompoundFluidIngredient;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.DataComponentFluidIngredient;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.DifferenceFluidIngredient;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.EmptyFluidIngredient;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.FluidIngredient;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.FluidIngredientType;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.IntersectionFluidIngredient;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.SingleFluidIngredient;
import io.github.fabricators_of_create.porting_lib.resources.fluids.crafting.TagFluidIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientImpl;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.AnyIngredient;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public class PortingLibIngredients {
	public static final ResourceKey<Registry<FluidIngredientType<?>>> FLUID_INGREDIENT_TYPE_KEY = PortingLib.key("fluid_ingredient_type");
	public static final Registry<FluidIngredientType<?>> FLUID_INGREDIENT_TYPES = new RegistryBuilder<>(FLUID_INGREDIENT_TYPE_KEY).sync(true).create();

	public static final FluidIngredientType<SingleFluidIngredient> SINGLE_FLUID_INGREDIENT_TYPE = register("single", new FluidIngredientType<>(SingleFluidIngredient.CODEC));
	public static final FluidIngredientType<TagFluidIngredient> TAG_FLUID_INGREDIENT_TYPE = register("tag", new FluidIngredientType<>(TagFluidIngredient.CODEC));
	public static final FluidIngredientType<EmptyFluidIngredient> EMPTY_FLUID_INGREDIENT_TYPE = register("empty", new FluidIngredientType<>(EmptyFluidIngredient.CODEC));
	public static final FluidIngredientType<CompoundFluidIngredient> COMPOUND_FLUID_INGREDIENT_TYPE = register("compound", new FluidIngredientType<>(CompoundFluidIngredient.CODEC));
	public static final FluidIngredientType<DataComponentFluidIngredient> DATA_COMPONENT_FLUID_INGREDIENT_TYPE = register("components", new FluidIngredientType<>(DataComponentFluidIngredient.CODEC));
	public static final FluidIngredientType<DifferenceFluidIngredient> DIFFERENCE_FLUID_INGREDIENT_TYPE = register("difference", new FluidIngredientType<>(DifferenceFluidIngredient.CODEC));
	public static final FluidIngredientType<IntersectionFluidIngredient> INTERSECTION_FLUID_INGREDIENT_TYPE = register("intersection", new FluidIngredientType<>(IntersectionFluidIngredient.CODEC));


	public static final IngredientType<DataComponentIngredient> DATA_COMPONENT_INGREDIENT_TYPE = new IngredientType<>(PortingLib.id("components"), DataComponentIngredient.CODEC);

	public static final MapCodec<Ingredient> INGREDIENT_MAP_CODEC_NONEMPTY = makeIngredientMapCodec();
	public static final MapCodec<Ingredient.ItemValue> ITEM_VALUE_MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(ItemStack.SIMPLE_ITEM_CODEC.fieldOf("item").forGetter(Ingredient.ItemValue::item))
					.apply(instance, Ingredient.ItemValue::new)
	);
	public static final MapCodec<Ingredient.TagValue> TAG_VALUE_MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(Ingredient.TagValue::tag))
					.apply(instance, Ingredient.TagValue::new)
	);
	public static final MapCodec<Ingredient.Value> VALUE_MAP_CODEC = PortingLibExtraCodecs.xor(ITEM_VALUE_MAP_CODEC, TAG_VALUE_MAP_CODEC)
			.xmap(p_300956_ -> p_300956_.map(itemValue -> itemValue, tagValue -> tagValue), value -> {
				if (value instanceof Ingredient.TagValue tagValue) {
					return Either.right(tagValue);
				} else if (value instanceof Ingredient.ItemValue itemValue) {
					return Either.left(itemValue);
				} else {
					throw new UnsupportedOperationException("This is neither an item value nor a tag value.");
				}
			});

	public static MapCodec<Ingredient> makeIngredientMapCodec() {
		// Dispatch codec for custom ingredient types, else fallback to vanilla ingredient codec.
		return PortingLibExtraCodecs.<CustomIngredientSerializer<?>, CustomIngredient, Ingredient.Value>dispatchMapOrElse(
						CustomIngredientImpl.TYPE_KEY,
						CustomIngredientImpl.CODEC,
						CustomIngredient::getSerializer,
						customIngredientSerializer -> customIngredientSerializer.getCodec(false),
						VALUE_MAP_CODEC)
				.xmap(either -> either.map(CustomIngredient::toVanilla, v -> Ingredient.fromValues(Stream.of(v))), ingredient -> {
					if (!ingredient.port_lib$isCustom()) {
						var values = ingredient.port_lib$getValues();
						if (values.length == 1) {
							return Either.right(values[0]);
						}
						// Convert vanilla ingredients with 2+ values to a CompoundIngredient. Empty ingredients are not allowed here.
						return Either.left(new AnyIngredient(Stream.of(ingredient.port_lib$getValues()).map(v -> Ingredient.fromValues(Stream.of(v))).toList()));
					}
					return Either.left(ingredient.getCustomIngredient());
				})
				.validate(ingredient -> {
					if (!ingredient.port_lib$isCustom() && ingredient.port_lib$getValues().length == 0) {
						return DataResult.error(() -> "Cannot serialize empty ingredient using the map codec");
					}
					return DataResult.success(ingredient);
				});
	}

	private static <T extends FluidIngredient> FluidIngredientType<T> register(String name, FluidIngredientType<T> type) {
		return Registry.register(FLUID_INGREDIENT_TYPES, PortingLib.id(name), type);
	}

	public static void init() {
		CustomIngredientSerializer.register(DATA_COMPONENT_INGREDIENT_TYPE);
	}
}
