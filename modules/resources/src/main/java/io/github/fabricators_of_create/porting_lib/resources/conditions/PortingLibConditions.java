package io.github.fabricators_of_create.porting_lib.resources.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public class PortingLibConditions {
	public static final ResourceKey<Registry<MapCodec<? extends ICondition>>> CONDITION_CODECS_KEY =  ResourceKey.createRegistryKey(PortingLib.id("condition_codecs"));
	public static final Registry<MapCodec<? extends ICondition>> CONDITION_SERIALIZERS = FabricRegistryBuilder.createSimple(CONDITION_CODECS_KEY).buildAndRegister();

	public static final Codec<Optional<WithConditions<Advancement>>> CONDITIONAL_ADVANCEMENTS_CODEC = ConditionalOps.createConditionalCodecWithConditions(Advancement.CODEC);
	public static final Codec<Optional<WithConditions<Recipe<?>>>> CONDITIONAL_RECIPES_CODEC = ConditionalOps.createConditionalCodecWithConditions(Recipe.CODEC);

	public static final MapCodec<AndCondition> AND_CONDITION = Registry.register(CONDITION_SERIALIZERS, PortingLib.neo("and"), AndCondition.CODEC);
	public static final MapCodec<FalseCondition> FALSE_CONDITION = Registry.register(CONDITION_SERIALIZERS, PortingLib.neo("false"), FalseCondition.CODEC);
	public static final MapCodec<ItemExistsCondition> ITEM_EXISTS_CONDITION = Registry.register(CONDITION_SERIALIZERS, PortingLib.neo("item_exists"), ItemExistsCondition.CODEC);
	public static final MapCodec<ModLoadedCondition> MOD_LOADED_CONDITION = Registry.register(CONDITION_SERIALIZERS, PortingLib.neo("mod_loaded"), ModLoadedCondition.CODEC);
	public static final MapCodec<NotCondition> NOT_CONDITION = Registry.register(CONDITION_SERIALIZERS, PortingLib.neo("not"), NotCondition.CODEC);
	public static final MapCodec<OrCondition> OR_CONDITION = Registry.register(CONDITION_SERIALIZERS, PortingLib.neo("or"), OrCondition.CODEC);
	public static final MapCodec<TagEmptyCondition> TAG_EMPTY_CONDITION = Registry.register(CONDITION_SERIALIZERS, PortingLib.neo("tag_empty"), TagEmptyCondition.CODEC);
	public static final MapCodec<TrueCondition> TRUE_CONDITION = Registry.register(CONDITION_SERIALIZERS, PortingLib.neo("true"), TrueCondition.CODEC);

	public static void init() {}
}
