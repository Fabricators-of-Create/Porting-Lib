package io.github.fabricators_of_create.porting_lib.resources.conditions;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import net.minecraft.world.flag.FeatureFlagSet;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ConditionContext implements ICondition.IContext {
	private final Map<ResourceKey<? extends Registry<?>>, HolderLookup.RegistryLookup<?>> pendingTags;
	private final FeatureFlagSet enabledFeatures;
	private final RegistryAccess registryAccess;

	public ConditionContext(List<Registry.PendingTags<?>> pendingTags, RegistryAccess registryAccess, FeatureFlagSet enabledFeatures) {
		this.pendingTags = new IdentityHashMap<>();
		this.registryAccess = registryAccess;
		this.enabledFeatures = enabledFeatures;

		for (var tags : pendingTags) {
			this.pendingTags.put(tags.key(), tags.lookup());
		}
	}

	public void clear() {
		this.pendingTags.clear();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> boolean isTagLoaded(TagKey<T> key) {
		var lookup = pendingTags.get(key.registry());
		return lookup != null && lookup.get((TagKey) key).isPresent();
	}

	@Override
	public RegistryAccess registryAccess() {
		return registryAccess;
	}

	@Override
	public FeatureFlagSet enabledFeatures() {
		return enabledFeatures;
	}
}
