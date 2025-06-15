package io.github.fabricators_of_create.porting_lib.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;
import net.fabricmc.fabric.impl.datagen.ForcedTagEntry;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;

import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PortingLibTagsProvider<T> extends TagsProvider<T> {
	protected final String modId;
	@Nullable
	protected final ExistingFileHelper existingFileHelper;
	private final ExistingFileHelper.IResourceType resourceType;
	private final ExistingFileHelper.IResourceType elementResourceType; // FORGE: Resource type for validating required references to datapack registry elements.

	protected PortingLibTagsProvider(FabricDataOutput output, ResourceKey<? extends Registry<T>> resourceKey, CompletableFuture<HolderLookup.Provider> provider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		this(output, resourceKey, provider, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()), modId, existingFileHelper);
	}

	protected PortingLibTagsProvider(FabricDataOutput output, ResourceKey<? extends Registry<T>> resourceKey, CompletableFuture<HolderLookup.Provider> provider, CompletableFuture<TagsProvider.TagLookup<T>> p_275565_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, resourceKey, provider);
		this.modId = modId;
		this.existingFileHelper = existingFileHelper;
		this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", Registries.tagsDirPath(resourceKey));
		this.elementResourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", PortingLibDataHack.prefixNamespace(resourceKey.location()));
	}

	// PL: Allow customizing the path for a given tag or returning null
	@org.jetbrains.annotations.Nullable
	protected Path getPath(ResourceLocation id) {
		return this.pathProvider.json(id);
	}

	@Override
	public String getName() {
		return "Tags for " + this.registryKey.location() + " mod id " + this.modId;
	}

	protected ResourceKey<T> reverseLookup(T element) {
		Registry registry = BuiltInRegistries.REGISTRY.get((ResourceKey) registryKey);

		if (registry != null) {
			Optional<Holder<T>> key = registry.getResourceKey(element);

			if (key.isPresent()) {
				return (ResourceKey<T>) key.get();
			}
		}

		throw new UnsupportedOperationException("Adding objects is not supported by " + getClass());
	}

	@Override
	protected PortingLibTagAppender tag(TagKey<T> tag) {
		return new PortingLibTagAppender(super.tag(tag));
	}

	@Override
	protected TagBuilder getOrCreateRawBuilder(TagKey<T> tag) {
		return this.builders.computeIfAbsent(tag.location(), (resourceLocation) -> new PortingLibTagBuilder());
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		record CombinedData<T>(HolderLookup.Provider contents, TagsProvider.TagLookup<T> parent) {
		}

		return this.createContentsProvider()
				.thenApply(provider -> {
					this.contentsDone.complete(null);
					return provider;
				})
				.thenCombineAsync(
						this.parentProvider, CombinedData::new, Util.backgroundExecutor()
				)
				.thenCompose(
						data -> {
							HolderLookup.RegistryLookup<T> lookup = data.contents.lookupOrThrow(this.registryKey);
							Predicate<ResourceLocation> hasRegistry = location -> lookup.get(ResourceKey.create(this.registryKey, location)).isPresent();
							Predicate<ResourceLocation> hasTag = location -> this.builders.containsKey(location)
									|| data.parent.contains(TagKey.create(this.registryKey, location));
							return CompletableFuture.allOf(
									this.builders
											.entrySet()
											.stream()
											.map(
													tagEntry -> {
														ResourceLocation tagId = tagEntry.getKey();
														TagBuilder builder = tagEntry.getValue();
														List<TagEntry> entries = builder.build();
														List<TagEntry> missingTags = entries.stream()
																.filter((entry) -> !entry.verifyIfPresent(hasRegistry, hasTag))
																.filter(this::missing)
																.toList();
														if (!missingTags.isEmpty()) {
															throw new IllegalArgumentException(
																	String.format(
																			Locale.ROOT,
																			"Couldn't define tag %s as it is missing following references: %s",
																			tagId,
																			missingTags.stream().map(Objects::toString).collect(Collectors.joining(","))
																	)
															);
														} else {
															Path path = getPath(tagId);
															if (path == null)
																return CompletableFuture.completedFuture(null); // PL: Allow running this data provider without writing it. Recipe provider needs valid tags.
															var removed = ((PortingLibTagBuilder) builder).getRemoveEntries().toList();
															return DataProvider.saveStable(output, data.contents, TagFile.CODEC, new TagFile(entries, ((PortingLibTagBuilder) builder).isReplace()/*, removed*/), path); // TODO: Remove not support for right now
														}
													}
											)
											.toArray(CompletableFuture[]::new)
							);
						}
				);
	}

	private boolean missing(TagEntry reference) {
		// Optional tags should not be validated

		if (reference.required) {
			return existingFileHelper == null || !existingFileHelper.exists(reference.id, reference.tag ? resourceType : elementResourceType);
		}
		return false;
	}

	public static class PortingLibTagBuilder extends TagBuilder {
		/**
		 * Remove entries are used for datagen.
		 */
		private final List<TagEntry> removeEntries = new ArrayList<>();
		private boolean replace = false;

		public Stream<TagEntry> getRemoveEntries() {
			return this.removeEntries.stream();
		}

		/**
		 * Add an entry to be removed from this tag in datagen.
		 */
		public TagBuilder remove(final TagEntry entry) {
			this.removeEntries.add(entry);
			return this;
		}

		public TagBuilder replace(boolean value) {
			this.replace = value;
			return this;
		}

		/**
		 * Shorthand version of replace(true)
		 */
		public TagBuilder replace() {
			return replace(true);
		}

		/**
		 * Is this tag set to replace or not?
		 */
		public boolean isReplace() {
			return this.replace;
		}
	}

	public class PortingLibTagAppender extends TagAppender<T> {
		protected final TagsProvider.TagAppender<T> parent;

		protected PortingLibTagAppender(TagAppender<T> parent) {
			super(parent.builder);
			this.parent = parent;
		}

		/**
		 * Set the value of the `replace` flag in a Tag.
		 *
		 * <p>When set to true the tag will replace any existing tag entries.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		public PortingLibTagAppender setReplace(boolean replace) {
			((PortingLibTagBuilder) builder).replace(replace);
			return this;
		}

		/**
		 * Add an element to the tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		public PortingLibTagAppender add(T element) {
			add(reverseLookup(element));
			return this;
		}

		/**
		 * Add multiple elements to the tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		@SafeVarargs
		public final PortingLibTagAppender add(T... element) {
			Stream.of(element).map(PortingLibTagsProvider.this::reverseLookup).forEach(this::add);
			return this;
		}

		/**
		 * Add an element to the tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 * @see #addTag(ResourceLocation)
		 */
		@Override
		public PortingLibTagAppender add(ResourceKey<T> registryKey) {
			parent.add(registryKey);
			return this;
		}

		/**
		 * Add a single element to the tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		public PortingLibTagAppender addTag(ResourceLocation id) {
			builder.addTag(id);
			return this;
		}

		/**
		 * Add an optional {@link ResourceLocation} to the tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		@Override
		public PortingLibTagAppender addOptional(ResourceLocation id) {
			parent.addOptional(id);
			return this;
		}

		/**
		 * Add an optional {@link ResourceKey} to the tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		public PortingLibTagAppender addOptional(ResourceKey<? extends T> registryKey) {
			return addOptional(registryKey.location());
		}

		/**
		 * Add another tag to this tag.
		 *
		 * <p><b>Note:</b> any vanilla tags can be added to the builder,
		 * but other tags can only be added if it has a builder registered in the same provider.
		 *
		 * <p>Use {@link #forceAddTag(TagKey)} to force add any tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 * @see BlockTags
		 * @see EntityTypeTags
		 * @see FluidTags
		 * @see GameEventTags
		 * @see ItemTags
		 */
		@Override
		public PortingLibTagAppender addTag(TagKey<T> tag) {
			builder.addTag(tag.location());
			return this;
		}

		/**
		 * Add another optional tag to this tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		@Override
		public PortingLibTagAppender addOptionalTag(ResourceLocation id) {
			parent.addOptionalTag(id);
			return this;
		}

		/**
		 * Add another optional tag to this tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		public PortingLibTagAppender addOptionalTag(TagKey<T> tag) {
			return addOptionalTag(tag.location());
		}

		/**
		 * Add another tag to this tag, ignoring any warning.
		 *
		 * <p><b>Note:</b> only use this method if you sure that the tag will be always available at runtime.
		 * If not, use {@link #addOptionalTag(ResourceLocation)} instead.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		public PortingLibTagAppender forceAddTag(TagKey<T> tag) {
			builder.add(new ForcedTagEntry(TagEntry.element(tag.location())));
			return this;
		}

		/**
		 * Add multiple elements to this tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		public PortingLibTagAppender add(ResourceLocation... ids) {
			for (ResourceLocation id : ids) {
				add(id);
			}

			return this;
		}

		/**
		 * Add multiple elements to this tag.
		 *
		 * @return the {@link PortingLibTagAppender} instance
		 */
		@SafeVarargs
		@Override
		public final PortingLibTagAppender add(ResourceKey<T>... registryKeys) {
			for (ResourceKey<T> registryKey : registryKeys) {
				add(registryKey);
			}

			return this;
		}
	}
}
