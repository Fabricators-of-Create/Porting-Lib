package io.github.fabricators_of_create.porting_lib;

import com.mojang.serialization.Codec;

import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class PortingLibRegistries {
	static final LazyRegistrar<Codec<? extends IGlobalLootModifier>> DEFERRED_GLOBAL_LOOT_MODIFIER_SERIALIZERS = LazyRegistrar.create(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS.location().getNamespace());
	public static final Supplier<Registry<Codec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = DEFERRED_GLOBAL_LOOT_MODIFIER_SERIALIZERS.makeRegistry();

	public static final class Keys {
		public static final ResourceKey<Registry<Codec<? extends IGlobalLootModifier>>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = key("global_loot_modifier_serializers");

		private static <T> ResourceKey<Registry<T>> key(String name) {
			return ResourceKey.createRegistryKey(PortingConstants.id(name));
		}
	}
}
