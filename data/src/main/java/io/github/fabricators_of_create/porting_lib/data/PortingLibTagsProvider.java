package io.github.fabricators_of_create.porting_lib.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;

import net.minecraft.tags.TagManager;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class PortingLibTagsProvider<T> extends FabricTagProvider<T> {
	@Nullable
	protected final ExistingFileHelper existingFileHelper;
	private final ExistingFileHelper.IResourceType resourceType;
	private final ExistingFileHelper.IResourceType elementResourceType; // Resource type for validating required references to datapack registry elements.

	/**
	 * Constructs a new {@link FabricTagProvider} with the default computed path.
	 *
	 * <p>Common implementations of this class are provided.
	 *
	 * @param output           the {@link FabricDataOutput} instance
	 * @param registryKey
	 * @param registriesFuture the backing registry for the tag type
	 */
	public PortingLibTagsProvider(FabricDataOutput output, ResourceKey<? extends Registry<T>> registryKey, CompletableFuture<HolderLookup.Provider> registriesFuture, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, registryKey, registriesFuture);
		this.existingFileHelper = existingFileHelper;
		this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", TagManager.getTagDir(registryKey));
		this.elementResourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", DatapackBuiltinEntriesProvider.prefixNamespace(registryKey.location()));
	}

	@Nullable
	protected Path getPath(ResourceLocation id) {
		return this.pathProvider.json(id);
	}

	public CompletableFuture<?> run(CachedOutput p_253684_) {
		return this.lookupProvider.thenCompose((p_255494_) -> {
			this.builders.clear();
			this.addTags(p_255494_);
			HolderLookup.RegistryLookup<T> registrylookup = p_255494_.lookup(this.registryKey).orElseThrow(() -> {
				return new IllegalStateException("Registry " + this.registryKey.location() + " not found");
			});
			Predicate<ResourceLocation> predicate = (p_255496_) -> {
				return registrylookup.get(ResourceKey.create(this.registryKey, p_255496_)).isPresent();
			};
			return CompletableFuture.allOf(this.builders.entrySet().stream().map((p_255499_) -> {
				ResourceLocation resourcelocation = p_255499_.getKey();
				TagBuilder tagbuilder = p_255499_.getValue();
				List<TagEntry> list = tagbuilder.build();
				List<TagEntry> list1 = list.stream().filter((p_255492_) -> {
					return !p_255492_.verifyIfPresent(predicate, this.builders::containsKey);
				}).filter(this::missing).toList(); // Forge: Add validation via existing resources
				if (!list1.isEmpty()) {
					throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s", resourcelocation, list1.stream().map(Objects::toString).collect(Collectors.joining(","))));
				} else {
					JsonElement jsonelement = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(list, false)).getOrThrow(false, LOGGER::error);
					Path path = this.getPath(resourcelocation);
					if (path == null) return CompletableFuture.completedFuture(null); // Allow running this data provider without writing it. Recipe provider needs valid tags.
					return DataProvider.saveStable(p_253684_, jsonelement, path);
				}
			}).toArray((p_253442_) -> {
				return new CompletableFuture[p_253442_];
			}));
		});
	}

	private boolean missing(TagEntry reference) {
		// Optional tags should not be validated

		if (reference.required) {
			return existingFileHelper == null || !existingFileHelper.exists(reference.id, reference.tag ? resourceType : elementResourceType);
		}
		return false;
	}
}
