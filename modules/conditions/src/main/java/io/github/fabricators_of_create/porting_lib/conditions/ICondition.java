package io.github.fabricators_of_create.porting_lib.conditions;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public interface ICondition extends ResourceCondition {

	@Override
	default boolean test(@Nullable HolderLookup.Provider registryLookup) {
		return test(registryLookup, IContext.TAGS_INVALID);
	}

	boolean test(@Nullable HolderLookup.Provider registryLookup, IContext context);


	interface IContext {
		IContext EMPTY = new IContext() {
			@Override
			public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
				return Collections.emptyMap();
			}
		};

		IContext TAGS_INVALID = new IContext() {
			@Override
			public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
				throw new UnsupportedOperationException("Usage of tag-based conditions is not permitted in this context!");
			}
		};

		/**
		 * Return the requested tag if available, or an empty tag otherwise.
		 */
		default <T> Collection<Holder<T>> getTag(TagKey<T> key) {
			return getAllTags(key.registry()).getOrDefault(key.location(), Set.of());
		}

		/**
		 * Return all the loaded tags for the passed registry, or an empty map if none is available.
		 * Note that the map and the tags are unmodifiable.
		 */
		<T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry);
	}
}
